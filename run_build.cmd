@echo off
REM Script para compilar y ejecutar MAZER VENTAS con JasperReports
REM Windows Batch (.cmd)

setlocal enabledelayedexpansion

cls
echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║   COMPILACION Y EJECUCION - JASPERREPORTS MAZER VENTAS        ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

REM Directorio del proyecto
set PROJECT_PATH=C:\Users\User\Documents\IntelijIdea\Mazer_Ventas
cd /d %PROJECT_PATH%

echo 📁 Directorio: %PROJECT_PATH%
echo.

REM Paso 1: Compilar
echo 1️⃣  Compilando con Maven...
echo    Comando: mvnw.cmd clean compile
echo.

call mvnw.cmd clean compile
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Error en compilacion
    pause
    exit /b 1
)

echo ✅ Compilacion exitosa
echo.

REM Paso 2: Ejecutar
echo 2️⃣  Iniciando aplicacion Spring Boot...
echo    Comando: mvnw.cmd spring-boot:run
echo.
echo ⏳ La aplicacion se iniciara en http://localhost:8080
echo.
echo Espera a ver: "Tomcat started on port(s): 8080"
echo.

call mvnw.cmd spring-boot:run

