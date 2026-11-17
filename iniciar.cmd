@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\iniciar.ps1" %*
exit /b %errorlevel%
