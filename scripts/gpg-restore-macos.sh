#!/usr/bin/env bash
# 通用 GPG 恢复脚本（macOS）
# 恢复内容基于 gpg-backup-macos.sh 生成的备份目录结构：
#  - secret-<KEYID>-<TS>.asc
#  - public-<KEYID>-<TS>.asc
#  - ownertrust-<TS>.txt
#  - gnupg-dir-<TS>.tar.gz（如果当时存在 ~/.gnupg 且有权限，则会有）

set -euo pipefail

BACKUP_ROOT="${HOME}/gpg-backups"
TIMENOW="$(date +%Y%m%d-%H%M%S)"

echo "===> GPG 恢复开始"

# 1. 检查 gpg 是否存在
if ! command -v gpg >/dev/null 2>&1; then
  echo "ERROR: 未找到 gpg 命令，请先安装 GnuPG（例如：brew install gnupg）"
  exit 1
fi

# 2. 确定要恢复的备份目录
if [ ! -d "${BACKUP_ROOT}" ]; then
  echo "ERROR: 备份根目录不存在：${BACKUP_ROOT}"
  exit 1
fi

BACKUP_DIR=""

if [ $# -ge 1 ]; then
  # 指定时间戳目录：如 ./gpg-restore-macos.sh 20251211-220358
  CANDIDATE="${BACKUP_ROOT}/$1"
  if [ ! -d "${CANDIDATE}" ]; then
    echo "ERROR: 指定的备份目录不存在：${CANDIDATE}"
    exit 1
  fi
  BACKUP_DIR="${CANDIDATE}"
else
  # 未指定则自动选最新的备份目录
  latest_dir="$(ls -1d "${BACKUP_ROOT}"/20* 2>/dev/null | sort | tail -n 1 || true)"
  if [ -z "${latest_dir}" ]; then
    echo "ERROR: 在 ${BACKUP_ROOT} 下未找到任何备份目录"
    exit 1
  fi
  BACKUP_DIR="${latest_dir}"
fi

echo "使用备份目录：${BACKUP_DIR}"
echo

# 3. 简单提示（可以按需要去掉交互）
echo "此操作将影响当前 GPG 配置："
echo "  1) 备份现有 ~/.gnupg"
echo "  2)（如有）从 gnupg-dir-*.tar.gz 恢复 ~/.gnupg 快照"
echo "  3) 从 secret-*.asc + ownertrust-*.txt 导入 key 和 trust"
read -r -p "是否继续？(y/N) " answer
case "${answer}" in
  [Yy]* ) echo "继续执行恢复..." ;;
  * ) echo "用户取消恢复。"; exit 0 ;;
esac

# 4. 先备份当前 ~/.gnupg（如果存在）
if [ -d "${HOME}/.gnupg" ]; then
  BACKUP_EXISTING="${BACKUP_ROOT}/existing-gnupg-backup-${TIMENOW}.tar.gz"
  echo
  echo "===> 备份当前 ~/.gnupg 到：${BACKUP_EXISTING}"
  tar czf "${BACKUP_EXISTING}" -C "${HOME}" ".gnupg"
else
  echo
  echo "===> 当前未发现 ~/.gnupg，跳过现有配置备份。"
fi

# 确保 ~/.gnupg 存在
mkdir -p "${HOME}/.gnupg"
chmod 700 "${HOME}/.gnupg"

# 5. 如果存在 gnupg-dir-*.tar.gz，优先恢复目录快照
GNUPG_TAR="$(ls "${BACKUP_DIR}"/gnupg-dir-*.tar.gz 2>/dev/null | head -n 1 || true)"

if [ -n "${GNUPG_TAR}" ]; then
  echo
  echo "===> 发现目录快照：${GNUPG_TAR}"
  echo "    将其内容恢复到 ~/.gnupg（会覆盖当前 ~/.gnupg 下已有同名文件）"

  TMPDIR="$(mktemp -d)"
  echo "    使用临时目录：${TMPDIR}"

  tar xzf "${GNUPG_TAR}" -C "${TMPDIR}"

  # 还原时假设 tar 里顶层目录名为 gnupg-copy（与备份脚本保持一致）
  if [ -d "${TMPDIR}/gnupg-copy" ]; then
    rsync -a "${TMPDIR}/gnupg-copy/" "${HOME}/.gnupg/"
  else
    echo "WARN: 没找到预期目录 ${TMPDIR}/gnupg-copy，跳过目录快照恢复。"
  fi

  rm -rf "${TMPDIR}"

  chmod 700 "${HOME}/.gnupg"
else
  echo
  echo "===> 未发现 gnupg-dir-*.tar.gz，跳过目录快照恢复（仅通过 key 导入恢复）。"
fi

# 6. 从 secret-*.asc 导入私钥（会包含对应 public 部分）
echo
echo "===> 从备份导入 secret keys..."

shopt -s nullglob
SECRET_FILES=("${BACKUP_DIR}"/secret-*.asc)
shopt -u nullglob

if [ "${#SECRET_FILES[@]}" -eq 0 ]; then
  echo "WARN: 未找到任何 secret-*.asc 文件，跳过私钥导入。"
else
  for SECRET_FILE in "${SECRET_FILES[@]}"; do
    echo "  导入私钥：${SECRET_FILE}"
    gpg --import "${SECRET_FILE}"
  done
fi

# 7. 导入 ownertrust
echo
echo "===> 导入 ownertrust（如存在）..."

OWNERTRUST_FILE="$(ls "${BACKUP_DIR}"/ownertrust-*.txt 2>/dev/null | head -n 1 || true)"

if [ -n "${OWNERTRUST_FILE}" ]; then
  echo "  使用 ownertrust 文件：${OWNERTRUST_FILE}"
  gpg --import-ownertrust "${OWNERTRUST_FILE}"
else
  echo "  未找到 ownertrust-*.txt，跳过 ownertrust 导入。"
fi

# 8. 简单检查：列出当前 secret keys
echo
echo "===> 恢复完成，当前 secret keys 如下："
gpg --list-secret-keys --keyid-format=long || true

echo
echo "提示："
echo "  - 如果你从旧机器迁移过来，现在可以在 Gradle / JReleaser 中继续使用同一套 key。"
echo "  - 建议再做一次新的备份确认流程没问题。"
