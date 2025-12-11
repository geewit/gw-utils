#!/usr/bin/env bash
# 通用 GPG 备份脚本（macOS）
# - 自动从 `gpg --list-secret-keys --keyid-format=long` 解析所有 secret key ID
# - 为每个 key 导出私钥、公钥
# - 导出 ownertrust
# - 通过 rsync 复制 ~/.gnupg（排除 sockets 等运行时文件），再打包

set -euo pipefail

BACKUP_ROOT="${HOME}/gpg-backups"
TIMESTAMP="$(date +%Y%m%d-%H%M%S)"
BACKUP_DIR="${BACKUP_ROOT}/${TIMESTAMP}"

echo "===> GPG 备份开始"
echo "备份目录: ${BACKUP_DIR}"
mkdir -p "${BACKUP_DIR}"

# 1. 确认 gpg 可用
if ! command -v gpg >/dev/null 2>&1; then
  echo "ERROR: 未找到 gpg 命令，请先安装 GnuPG（例如：brew install gnupg）"
  exit 1
fi

# 2. 解析所有 secret key 的 key id（从 `sec` 行中提取 / 后面的部分）
echo "===> 检查本地 secret keys..."
KEY_IDS="$(gpg --list-secret-keys --keyid-format=long 2>/dev/null | \
  awk '/^sec/{split($2,a,"/"); print a[2]}')"

if [ -z "${KEY_IDS}" ]; then
  echo "WARN: 未找到任何 secret key，退出。"
  exit 0
fi

echo "发现以下 secret key id："
printf '  - %s\n' ${KEY_IDS}
echo

# 3. 为每一把 key 导出私钥、公钥
for KEY_ID in ${KEY_IDS}; do
  echo "===> 导出 key: ${KEY_ID}"

  SECRET_OUT="${BACKUP_DIR}/secret-${KEY_ID}-${TIMESTAMP}.asc"
  PUBLIC_OUT="${BACKUP_DIR}/public-${KEY_ID}-${TIMESTAMP}.asc"

  echo "  导出私钥到: ${SECRET_OUT}"
  gpg --armor --export-secret-keys "${KEY_ID}" > "${SECRET_OUT}"

  echo "  导出公钥到: ${PUBLIC_OUT}"
  gpg --armor --export "${KEY_ID}" > "${PUBLIC_OUT}"
done

# 4. 导出 ownertrust（可选，但很有用）
OWNERTRUST_OUT="${BACKUP_DIR}/ownertrust-${TIMESTAMP}.txt"
echo
echo "===> 导出 ownertrust 到: ${OWNERTRUST_OUT}"
gpg --export-ownertrust > "${OWNERTRUST_OUT}"

# 5. 通过 rsync 复制 ~/.gnupg（排除 sockets 等运行时文件），再打包
if [ -d "${HOME}/.gnupg" ]; then
  echo
  echo "===> 准备 ~/.gnupg 目录副本（排除 sockets 等临时文件）"

  # 临时副本目录放在本次备份目录下，避免污染其他地方
  GNUPG_COPY_DIR="${BACKUP_DIR}/gnupg-copy"
  mkdir -p "${GNUPG_COPY_DIR}"

  # rsync 说明：
  # -a              : 保留权限、时间戳等
  # --exclude 'S.*' : 排除所有以 S. 开头的运行时 socket（S.gpg-agent、S.dirmngr 等）
  # --exclude '*.lock' : 排除锁文件
  # 你也可以按需继续添加其它 exclude 规则
  if ! command -v rsync >/dev/null 2>&1; then
    echo "ERROR: 未找到 rsync，请安装后再执行（macOS 通常默认带有 rsync）。"
    exit 1
  fi

  rsync -a \
    --exclude 'S.*' \
    --exclude '*.lock' \
    "${HOME}/.gnupg/" "${GNUPG_COPY_DIR}/"

  GNUPG_TAR="${BACKUP_DIR}/gnupg-dir-${TIMESTAMP}.tar.gz"
  echo "===> 打包 gnupg-copy 到: ${GNUPG_TAR}"
  tar czf "${GNUPG_TAR}" -C "${BACKUP_DIR}" "gnupg-copy"

  # 如不需要保留明文目录，可在打包后删除副本
  rm -rf "${GNUPG_COPY_DIR}"
else
  echo
  echo "WARN: 未找到目录 ${HOME}/.gnupg，跳过目录打包。"
fi

echo
echo "===> GPG 备份完成。所有文件已保存到:"
echo "  ${BACKUP_DIR}"
echo
echo "请妥善保存这些备份文件（尤其是 secret-*.asc 和 gnupg-dir-*.tar.gz），"
echo "建议放在加密磁盘或安全的离线介质上。"
