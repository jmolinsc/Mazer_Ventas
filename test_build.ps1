# Script de prueba para compilar y ejecutar JasperReports
Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║   COMPILACIÓN Y EJECUCIÓN - JASPERREPORTS MAZER VENTAS     ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

$projectPath = "C:\Users\User\Documents\IntelijIdea\Mazer_Ventas"
Set-Location $projectPath

Write-Host "📁 Directorio: $projectPath" -ForegroundColor Green
Write-Host ""

# Paso 1: Limpiar y compilar
Write-Host "1️⃣  Compilando con Maven..." -ForegroundColor Yellow
Write-Host "   Comando: .\mvnw.cmd clean compile" -ForegroundColor Gray
Write-Host ""

$compileResult = & ".\mvnw.cmd" clean compile 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilación exitosa" -ForegroundColor Green
} else {
    Write-Host "❌ Error en compilación" -ForegroundColor Red
    Write-Host $compileResult | Select-Object -Last 20
    exit 1
}

Write-Host ""
Write-Host "2️⃣  Iniciando aplicación Spring Boot..." -ForegroundColor Yellow
Write-Host "   Comando: .\mvnw.cmd spring-boot:run" -ForegroundColor Gray
Write-Host ""
Write-Host "⏳ La aplicación se iniciará en http://localhost:8080" -ForegroundColor Cyan
Write-Host ""

# Paso 2: Ejecutar
& ".\mvnw.cmd" spring-boot:run

