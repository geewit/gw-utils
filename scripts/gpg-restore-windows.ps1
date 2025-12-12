Param(
    # Optional: backup directory name like 20251211-220620
    [string]$BackupTimestamp
)

$ErrorActionPreference = "Stop"

Write-Host "===> GPG restore on Windows started"

# 1. Find gpg.exe
$gpg = Get-Command gpg.exe -ErrorAction SilentlyContinue
if (-not $gpg) {
    Write-Error "gpg.exe not found in PATH. Please install Gpg4win/GnuPG first."
    exit 1
}

# 2. Backup root = directory of this script
$ScriptRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$BackupRoot = $ScriptRoot
$TimeNow    = Get-Date -Format "yyyyMMdd-HHmmss"

Write-Host "Script / backup root: $BackupRoot"

if (-not (Test-Path $BackupRoot)) {
    Write-Error "Backup root does not exist: $BackupRoot"
    exit 1
}

# 3. Choose backup directory
$BackupDir = $null
if ($BackupTimestamp) {
    $candidate = Join-Path $BackupRoot $BackupTimestamp
    if (-not (Test-Path $candidate)) {
        Write-Error "Specified backup directory does not exist: $candidate"
        exit 1
    }
    $BackupDir = $candidate
} else {
    # auto pick latest directory whose name looks like 20251211-220620
    $dir = Get-ChildItem -Path $BackupRoot -Directory |
        Where-Object { $_.Name -match '^\d{8}-\d{6}$' } |
        Sort-Object Name |
        Select-Object -Last 1

    if (-not $dir) {
        Write-Error "No backup directory found under $BackupRoot (expected names like 20251211-220620)."
        exit 1
    }
    $BackupDir = $dir.FullName
}

Write-Host "Using backup directory: $BackupDir"
Write-Host ""

# 4. Windows GnuPG home
$GnuPgHome = Join-Path $env:APPDATA "gnupg"
Write-Host "Windows GnuPG home: $GnuPgHome"

# 5. Backup current gnupg
if (Test-Path $GnuPgHome) {
    $existingBackup = Join-Path $BackupRoot "existing-win-gnupg-$TimeNow.zip"
    Write-Host "===> Backup current gnupg to: $existingBackup"
    Compress-Archive -Path $GnuPgHome -DestinationPath $existingBackup -Force
} else {
    Write-Host "===> No existing $GnuPgHome directory found, skip current gnupg backup"
}

if (-not (Test-Path $GnuPgHome)) {
    New-Item -ItemType Directory -Path $GnuPgHome | Out-Null
}
Write-Host "Make sure you own $GnuPgHome and have read/write permission."

# 6. Restore gnupg directory snapshot if exists
$gnupgTar = Get-ChildItem -Path $BackupDir -Filter "gnupg-dir-*.tar.gz" -ErrorAction SilentlyContinue | Select-Object -First 1
if ($gnupgTar) {
    Write-Host ""
    Write-Host "===> Found gnupg dir archive: $($gnupgTar.FullName)"

    $tmpDir = Join-Path $env:TEMP ("gpg-restore-" + $TimeNow)
    New-Item -ItemType Directory -Path $tmpDir | Out-Null
    Write-Host "     Extracting to temp dir: $tmpDir"

    # Windows 10/11 has tar.exe
    & tar.exe -xzf $gnupgTar.FullName -C $tmpDir

    $src = Join-Path $tmpDir "gnupg-copy"
    if (Test-Path $src) {
        Write-Host "     Copying from gnupg-copy to $GnuPgHome"
        Copy-Item -Path (Join-Path $src "*") -Destination $GnuPgHome -Recurse -Force
    } else {
        Write-Warning "Expected directory '$src' not found. Skip gnupg dir restore."
    }

    Remove-Item -Path $tmpDir -Recurse -Force
} else {
    Write-Host ""
    Write-Host "===> No gnupg-dir-*.tar.gz found. Only importing keys and ownertrust."
}

# 7. Import secret keys
Write-Host ""
Write-Host "===> Importing secret keys (secret-*.asc) from backup..."

$secretFiles = Get-ChildItem -Path $BackupDir -Filter "secret-*.asc" -ErrorAction SilentlyContinue
if (-not $secretFiles) {
    Write-Warning "No secret-*.asc files found. Skipping secret key import."
} else {
    foreach ($sf in $secretFiles) {
        Write-Host "  Import: $($sf.Name)"
        & $gpg.Source --homedir "$GnuPgHome" --import "$($sf.FullName)"
    }
}

# 8. Import ownertrust
Write-Host ""
Write-Host "===> Importing ownertrust (ownertrust-*.txt) from backup..."

$ownertrust = Get-ChildItem -Path $BackupDir -Filter "ownertrust-*.txt" -ErrorAction SilentlyContinue | Select-Object -First 1
if ($ownertrust) {
    Write-Host "  Using: $($ownertrust.Name)"
    & $gpg.Source --homedir "$GnuPgHome" --import-ownertrust "$($ownertrust.FullName)"
} else {
    Write-Host "  No ownertrust-*.txt found. Skipping ownertrust import."
}

# 9. Show result
Write-Host ""
Write-Host "===> Restore finished. Current Windows secret keys:"
& $gpg.Source --homedir "$GnuPgHome" --list-secret-keys --keyid-format=long

Write-Host ""
Write-Host "Done. Gradle / JReleaser will use these keys via default gpg configuration."
