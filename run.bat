@echo off
setlocal enableextensions enabledelayedexpansion

echo Running a website searcher application

IF EXIST "%JAVA_HOME%"  goto javaExists

if "x%~1" == "x" (
  echo Please specify location of java 8 JDK distribution as a parameter
  echo Ex: build-jar "C:\env\jdk1.8"
  goto end
)

echo Setting JAVA_HOME to %~1
set JAVA_HOME=%~1

echo Adding JAVA_HOME to Path
set PATH=%JAVA_HOME%\bin

:javaExists
echo JAVA_HOME = %JAVA_HOME%
echo PATH = %PATH%

java -cp target/website-searcher-1.0-SNAPSHOT.jar org.junit.runner.JUnitCore RunAllTests

java -jar target\website-searcher-1.0-SNAPSHOT.jar app.WebContentsSearcher


:end
