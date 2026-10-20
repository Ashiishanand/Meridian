@echo off
setlocal
if "%MERIDIAN_DB_USER%"=="" set /p MERIDIAN_DB_USER=MySQL username [root]: 
if "%MERIDIAN_DB_USER%"=="" set MERIDIAN_DB_USER=root
if "%MERIDIAN_DB_PASSWORD%"=="" set /p MERIDIAN_DB_PASSWORD=MySQL password: 
java -jar meridian-1.0.0.jar
pause