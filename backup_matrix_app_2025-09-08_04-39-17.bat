@echo off
setlocal enabledelayedexpansion

REM Matrix App Backup Script
REM Creates a timestamped backup excluding gitignore/cursorignore files
REM Compresses with 7zip and saves to C:\Users\jjdog\Documents\app backups

echo ========================================
echo Matrix App Backup Script
echo ========================================

REM Get current date and time for backup folder name
for /f "tokens=2 delims==" %%a in ('wmic OS Get localdatetime /value') do set "dt=%%a"
set "YY=%dt:~2,2%" & set "YYYY=%dt:~0,4%" & set "MM=%dt:~4,2%" & set "DD=%dt:~6,2%"
set "HH=%dt:~8,2%" & set "Min=%dt:~10,2%"
set "timestamp=%YY%%MM%%DD%_%HH%%Min%"

REM Set paths
set "source_dir=%~dp0"
REM Remove trailing backslash from source_dir
set "source_dir=%source_dir:~0,-1%"
set "backup_base=C:\Users\jjdog\Documents\app backups"
set "backup_folder=%backup_base%\matrix_backup_%timestamp%"
set "backup_zip=%backup_base%\matrix_backup_%timestamp%.zip"
set "sevenzip_path=C:\Program Files\7-Zip\7z.exe"

echo Source directory: %source_dir%
echo Backup folder: %backup_folder%
echo Backup zip: %backup_zip%

REM Create backup base directory if it doesn't exist
if not exist "%backup_base%" (
    echo Creating backup directory: %backup_base%
    mkdir "%backup_base%"
)

REM Create timestamped backup folder (remove if exists)
if exist "%backup_folder%" (
    echo Removing existing backup folder: %backup_folder%
    rmdir /s /q "%backup_folder%"
)
echo Creating backup folder: %backup_folder%
mkdir "%backup_folder%"

REM Check if 7zip is available
if not exist "%sevenzip_path%" (
    echo ERROR: 7zip not found at %sevenzip_path%
    echo Please install 7zip or update the path in this script
    pause
    exit /b 1
)

echo Found 7zip at: %sevenzip_path%
echo.
echo Starting backup process...
echo.

REM Copy files while excluding gitignore/cursorignore patterns
echo Copying files (excluding build artifacts and temporary files)...

robocopy "%source_dir%" "%backup_folder%" /E /XD .gradle build .idea .cxx .kotlinc node_modules /XF *.iml .DS_Store Thumbs.db local.properties *.apk *.aab *.zip *.tar *.tar.gz *.keystore *.jks *.pem *.p12 *.key .env secrets.* /R:3 /W:1 /NFL /NDL /NJH /NJS /nc /ns /np

REM Check robocopy exit codes
if %errorlevel% geq 8 (
    echo ERROR: Robocopy failed with error code %errorlevel%
    pause
    exit /b 1
) else if %errorlevel% geq 4 (
    echo WARNING: Some files were not copied (error code %errorlevel%)
) else (
    echo Files copied successfully
)

echo.
echo Compressing backup with 7zip...

REM Compress the backup folder
"%sevenzip_path%" a -tzip "%backup_zip%" "%backup_folder%\*" -mx=9

if %errorlevel% neq 0 (
    echo ERROR: 7zip compression failed
    pause
    exit /b 1
)

echo.
echo Cleaning up temporary backup folder...
rmdir /s /q "%backup_folder%"

echo.
echo ========================================
echo Backup completed successfully!
echo ========================================
echo Backup saved as: %backup_zip%
echo.

REM Get file size for display
for %%A in ("%backup_zip%") do set "size=%%~zA"
set /a "size_mb=!size!/1024/1024"
echo Backup size: !size_mb! MB

echo.
echo Press any key to exit...
pause >nul