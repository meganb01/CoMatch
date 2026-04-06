@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF)
@REM Maven start up batch script for Windows
@REM ----------------------------------------------------------------------------

@REM Begin all REM://
@echo off
@REM set title of command window
title %0
@REM enable echoing by setting MAVEN_BATCH_ECHO to 'on'
@if "%MAVEN_BATCH_ECHO%"=="" @echo off

@REM set %HOME% to equivalent of $HOME
if "%HOME%"=="" (set "HOME=%HOMEDRIVE%%HOMEPATH%")

@REM Execute a user defined script before this one
if not "%MAVEN_SKIP_RC%"=="" goto skipRcPre
@REM check for pre script, once with hierarchical, once with fallback
if exist "%USERPROFILE%\mavenrc_pre.cmd" call "%USERPROFILE%\mavenrc_pre.cmd" %*
if exist "%USERPROFILE%\mavenrc_pre.bat" call "%USERPROFILE%\mavenrc_pre.bat" %*
:skipRcPre

@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%"=="" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:init

@REM Find the project base dir
set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

set EXEC_DIR=%CD%
set WDIR=%EXEC_DIR%
:findBaseDir
IF EXIST "%WDIR%"\.mvn goto baseDirFound
cd ..
IF "%WDIR%"=="%CD%" goto baseDirNotFound
set WDIR=%CD%
goto findBaseDir

:baseDirFound
set MAVEN_PROJECTBASEDIR=%WDIR%
cd "%EXEC_DIR%"
goto endDetectBaseDir

:baseDirNotFound
set MAVEN_PROJECTBASEDIR=%EXEC_DIR%
cd "%EXEC_DIR%"

:endDetectBaseDir

IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties" goto endWrapper

@REM Download maven-wrapper.jar if not present
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"

@REM Provide a simplified wrapper: download Maven directly
for /f "tokens=2 delims==" %%a in ('findstr /i "distributionUrl" "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties"') do set DOWNLOAD_URL=%%a

set MAVEN_HOME=%HOME%\.m2\wrapper\dists\apache-maven-3.9.6
if exist "%MAVEN_HOME%\bin\mvn.cmd" goto runMaven

echo Downloading Maven...
mkdir "%MAVEN_HOME%" 2>nul

powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $url='%DOWNLOAD_URL%'; $out='%HOME%\.m2\wrapper\dists\maven.zip'; (New-Object Net.WebClient).DownloadFile($url.Trim(), $out); Expand-Archive -Path $out -DestinationPath '%HOME%\.m2\wrapper\dists' -Force; Remove-Item $out }"

@REM Move extracted inner directory contents up
for /d %%d in ("%HOME%\.m2\wrapper\dists\apache-maven-*") do (
  if not "%%d"=="%MAVEN_HOME%" (
    xcopy /E /Y /Q "%%d\*" "%MAVEN_HOME%\" >nul
    rmdir /S /Q "%%d" 2>nul
  )
)

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  echo Error: Maven download or extraction failed.
  goto error
)

:runMaven
set MAVEN_CMD="%MAVEN_HOME%\bin\mvn.cmd"

:endWrapper

IF "%MAVEN_CMD%"=="" set MAVEN_CMD=mvn

%MAVEN_CMD% %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

cmd /C exit /B %ERROR_CODE%
