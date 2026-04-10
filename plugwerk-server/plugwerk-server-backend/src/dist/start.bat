@echo off
rem ---------------------------------------------------------------------------
rem Plugwerk Server — Start Script (Windows)
rem ---------------------------------------------------------------------------
rem Usage:
rem   start.bat                              Start with defaults
rem   set JAVA_OPTS=-Xmx2g && start.bat     Override JVM options
rem ---------------------------------------------------------------------------

setlocal enabledelayedexpansion

set "SCRIPT_DIR=%~dp0"

rem Find the server JAR
set "JAR="
for %%f in ("%SCRIPT_DIR%plugwerk-server-backend-*.jar") do set "JAR=%%f"

if "%JAR%"=="" (
    echo ERROR: No plugwerk-server-backend-*.jar found in %SCRIPT_DIR%
    exit /b 1
)

rem JVM defaults — override with JAVA_OPTS environment variable
if "%JAVA_OPTS%"=="" set "JAVA_OPTS=-Xms256m -Xmx512m -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError -Dfile.encoding=UTF-8"

echo Starting Plugwerk Server...
echo   JAR:       %JAR%
echo   JAVA_OPTS: %JAVA_OPTS%
echo.

java %JAVA_OPTS% -jar "%JAR%" %*
