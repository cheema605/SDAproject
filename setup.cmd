@echo off
REM Setup script to configure Maven wrapper environment for Windows

echo Detecting Java installation...

REM Try common Eclipse Adoptium locations
if exist "C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot" (
    set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.1.8-hotspot"
    echo Found Java at: %JAVA_HOME%
    goto :setup_complete
)

if exist "C:\Program Files\Java\jdk-23" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-23"
    echo Found Java at: %JAVA_HOME%
    goto :setup_complete
)

if exist "%JAVA_HOME%" (
    echo JAVA_HOME already set to: %JAVA_HOME%
    goto :setup_complete
)

echo Error: Could not find Java installation
echo Please set JAVA_HOME environment variable manually
exit /b 1

:setup_complete
echo.
echo Maven wrapper environment configured successfully
echo You can now run Maven commands:
echo   .\mvnw.cmd compile
echo   .\mvnw.cmd test
echo   .\mvnw.cmd javafx:run
