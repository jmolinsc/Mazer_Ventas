# Script de prueba de endpoints JasperReports
# Asegurate de que la aplicación esté ejecutándose en http://localhost:8080

$baseUrl = "http://localhost:8080"
$reportsUrl = "$baseUrl/reportes"

Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║        PRUEBA DE ENDPOINTS - JASPERREPORTS                ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Prueba 1: Health Check
Write-Host "1️⃣  Health Check" -ForegroundColor Yellow
Write-Host "   GET $reportsUrl/health" -ForegroundColor Gray
try {
    $response = Invoke-WebRequest -Uri "$reportsUrl/health" -Method Get -UseBasicParsing
    Write-Host "✅ Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "   Response: $($response.Content)" -ForegroundColor White
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Prueba 2: Descargar PDF
Write-Host "2️⃣  Descargar PDF de Venta ID=1" -ForegroundColor Yellow
Write-Host "   GET $reportsUrl/venta/1/pdf" -ForegroundColor Gray
try {
    $pdfPath = "$($env:USERPROFILE)\Downloads\venta_test_1.pdf"
    Invoke-WebRequest -Uri "$reportsUrl/venta/1/pdf" -Method Get -OutFile $pdfPath
    $fileSize = (Get-Item $pdfPath).Length
    Write-Host "✅ PDF descargado exitosamente" -ForegroundColor Green
    Write-Host "   Ubicación: $pdfPath" -ForegroundColor White
    Write-Host "   Tamaño: $(($fileSize/1KB).ToString('F2')) KB" -ForegroundColor White
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Prueba 3: Preview en navegador
Write-Host "3️⃣  Preview PDF en navegador" -ForegroundColor Yellow
Write-Host "   GET $reportsUrl/venta/1/preview" -ForegroundColor Gray
Write-Host "   (Abre en navegador en 2 segundos...)" -ForegroundColor Gray
Start-Sleep -Seconds 2
try {
    Start-Process "$reportsUrl/venta/1/preview"
    Write-Host "✅ Abriendo en navegador..." -ForegroundColor Green
} catch {
    Write-Host "❌ Error al abrir navegador: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Prueba 4: Excel de todas las ventas
Write-Host "4️⃣  Exportar Excel de todas las ventas" -ForegroundColor Yellow
Write-Host "   GET $reportsUrl/ventas/excel" -ForegroundColor Gray
try {
    $timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
    $excelPath = "$($env:USERPROFILE)\Downloads\ventas_$timestamp.xlsx"
    Invoke-WebRequest -Uri "$reportsUrl/ventas/excel" -Method Get -OutFile $excelPath
    $fileSize = (Get-Item $excelPath).Length
    Write-Host "✅ Excel descargado exitosamente" -ForegroundColor Green
    Write-Host "   Ubicación: $excelPath" -ForegroundColor White
    Write-Host "   Tamaño: $(($fileSize/1KB).ToString('F2')) KB" -ForegroundColor White
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

# Prueba 5: PDF por tipo de documento
Write-Host "5️⃣  Descargar FACTURA (con comportamiento específico)" -ForegroundColor Yellow
Write-Host "   GET $reportsUrl/venta/1/pdf/FACTURA" -ForegroundColor Gray
try {
    $facturarPath = "$($env:USERPROFILE)\Downloads\FACTURA_1.pdf"
    Invoke-WebRequest -Uri "$reportsUrl/venta/1/pdf/FACTURA" -Method Get -OutFile $facturarPath
    $fileSize = (Get-Item $facturarPath).Length
    Write-Host "✅ Factura descargada exitosamente" -ForegroundColor Green
    Write-Host "   Ubicación: $facturarPath" -ForegroundColor White
    Write-Host "   Tamaño: $(($fileSize/1KB).ToString('F2')) KB" -ForegroundColor White
} catch {
    Write-Host "❌ Error: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "╔════════════════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║              PRUEBAS COMPLETADAS                          ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════════════════════════╝" -ForegroundColor Green
Write-Host ""
Write-Host "📝 Notas:" -ForegroundColor Cyan
Write-Host "  • Los PDFs descargados están en: $($env:USERPROFILE)\Downloads" -ForegroundColor White
Write-Host "  • Verifica que las ventas existan en la BD (ID=1, etc.)" -ForegroundColor White
Write-Host "  • Los endpoints funcionan si la BD tiene datos de ventas" -ForegroundColor White

