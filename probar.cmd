@echo off
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0scripts\probar.ps1"
exit /b %errorlevel%
