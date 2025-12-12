#!/usr/bin/env bash
# WSL / Linux GPG 恢复脚本
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKUP_ROOT="${SCRIPT_DIR}"
TIMENOW="$(date +%Y%m%d-%H%M%S)"

echo "===> WSL/Linux 上 GPG 恢复开始"
echo "备份根目录: ${BACKUP_ROOT}"

if ! command -v gpg >/dev/null 2>&1; then
  echo "ERROR: 未找到 gpg 命令，请先安装 GnuPG" >&2
  exit 1
fi

# 1. 选择备份目录
BACKUP_DIR=""
if [ $# -ge 1 ]; then
  CANDIDATE="${BACKUP_ROOT}/$1"
  if [ ! -d "${CANDIDATE}" ]; then
    echo "ERROR: 指定的备份目录不存在: ${CANDIDATE}" >&2
    exit 1
  fi
  BACKUP_DIR="${CANDIDATE}"
else
  latest_dir="$(find "${BACKUP_ROOT}" -maxdepth 1 -mindepth 1 -type d -name '20*' 2>/dev/null | sort | tail -n 1 || true)"
  if [ -z "${latest_dir}" ]; then
    echo "ERROR: 在 ${BACKUP_ROOT} 下未找到任何备份目录" >&2
    exit 1
  fi
  BACKUP_DIR="${latest_dir}"
fi

echo "使用备份目录: ${BACKUP_DIR}"
echo

# 2. 先备份当前 ~/.gnupg
GNUPG_DIR="${HOME}/.gnupg"
if [ -d "${GNUPG_DIR}" ]; then
  BACKUP_EXISTING="${BACKUP_ROOT}/existing-linux-gnupg-${TIMENOW}.tar.gz"
  echo "===> 备份当前 ~/.gnupg 到: ${BACKUP_EXISTING}"
  tar czf "${BACKUP_EXISTING}" -C "${HOME}" ".gnupg"
else
  echo "===> 当前未发现 ~/.gnupg，跳过现有配置备份"
fi

mkdir -p "${GNUPG_DIR}"
chmod 700 "${GNUPG_DIR}"

# 3. 如果有 gnupg-dir-*.tar.gz，恢复目录快照
GNUPG_TAR="$(ls "${BACKUP_DIR}"/gnupg-dir-*.tar.gz 2>/dev/null | head -n 1 || true)"
if [ -n "${GNUPG_TAR}" ]; then
  echo
  echo "===> 发现目录快照: ${GNUPG_TAR}"
  TMPDIR="$(mktemp -d)"
  echo "    解压到临时目录: ${TMPDIR}"
  tar xzf "${GNUPG_TAR}" -C "${TMPDIR}"

  if [ -d "${TMPDIR}/gnupg-copy" ]; then
    echo "    从 gnupg-copy 覆盖到 ${GNUPG_DIR}"
    rsync -a "${TMPDIR}/gnupg-copy/" "${GNUPG_DIR}/"
  else
    echo "WARN: 未找到 ${TMPDIR}/gnupg-copy，跳过目录快照恢复"
  fi

  rm -rf "${TMPDIR}"
else
  echo
  echo "===> 未发现 gnupg-dir-*.tar.gz，跳过目录快照恢复（仅导入 key 与 ownertrust）"
fi

# 4. 导入 secret keys
echo
echo "===> 从备份导入私钥 (secret-*.asc)..."

shopt -s nullglob
SECRET_FILES=("${BACKUP_DIR}"/secret-*.asc)
shopt -u nullglob

if [ "${#SECRET_FILES[@]}" -eq 0 ]; then
  echo "WARN: 未找到 secret-*.asc 文件，跳过私钥导入"
else
  for SECRET_FILE in "${SECRET_FILES[@]}"; do
    echo "  导入: ${SECRET_FILE}"
    gpg --import "${SECRET_FILE}"
  done
fi

# 5. 导入 ownertrust
echo
echo "===> 导入 ownertrust (ownertrust-*.txt)..."
OWNERTRUST_FILE="$(ls "${BACKUP_DIR}"/ownertrust-*.txt 2>/dev/null | head -n 1 || true)"

if [ -n "${OWNERTRUST_FILE}" ]; then
  echo "  使用: ${OWNERTRUST_FILE}"
  gpg --import-ownertrust "${OWNERTRUST_FILE}"
else
  echo "  未找到 ownertrust-*.txt，跳过 ownertrust 导入"
fi

# 6. 检查
echo
echo "===> 恢复完成，当前 WSL/Linux secret keys:"
gpg --list-secret-keys --keyid-format=long || true
