@echo off
setlocal enableextensions enabledelayedexpansion

echo Building a jar from sources

IF EXIST target\ (
    rmdir /Q /s target
)

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

dir /s /B src\*.java > sources.txt

md target
md target\classes

javac -target 1.8 -source 1.8 -cp lib\junit-4.12.jar;lib\takari-cpsuite-1.2.7.jar;lib\hamcrest-core-1.3.jar -d target\classes @sources.txt

copy .\src\main\resources\config.properties target\classes\.

echo Manifest-Version: 1.0 > MANIFEST.MF
echo Class-Path: ../lib/junit-4.12.jar ../lib\takari-cpsuite-1.2.7.jar ../lib\hamcrest-core-1.3.jar >> MANIFEST.MF
echo Main-Class: app.WebContentsSearcher >> MANIFEST.MF

cd target\classes
jar cmf ..\..\MANIFEST.MF ..\website-searcher-1.0-SNAPSHOT.jar .

echo jar file with name website-searcher-1.0-SNAPSHOT.jar created in target folder

:end

cd ..\..
IF EXIST MANIFEST.MF (
    del /Q /F MANIFEST.MF
    del /Q /F sources.txt
)
