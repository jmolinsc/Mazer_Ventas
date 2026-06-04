#!/bin/bash
# SCRIPT DE TESTING - SISTEMA DE REPORTES MAZER VENTAS
# =====================================================

echo "🚀 INICIANDO TESTING DEL SISTEMA DE REPORTES"
echo ""

BASE_URL="http://localhost:8080"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Test 1: Verificar que servidor está activo
echo -e "${BLUE}[TEST 1]${NC} Verificando servidor..."
if curl -s "$BASE_URL/ventas/listar" > /dev/null 2>&1; then
    echo -e "${GREEN}✅ Servidor activo${NC}"
else
    echo -e "${RED}❌ Servidor no responde${NC}"
    exit 1
fi

# Test 2: Descargar PDF
echo ""
echo -e "${BLUE}[TEST 2]${NC} Descargando PDF de venta ID=1..."
PDF_OUTPUT="venta_1.pdf"
HTTP_CODE=$(curl -s -w "%{http_code}" -o "$PDF_OUTPUT" \
    -H "Accept: application/pdf" \
    "$BASE_URL/reportes/venta/1/pdf")

if [ "$HTTP_CODE" = "200" ]; then
    FILE_SIZE=$(ls -lh "$PDF_OUTPUT" | awk '{print $5}')
    echo -e "${GREEN}✅ PDF descargado: $PDF_OUTPUT ($FILE_SIZE)${NC}"
else
    echo -e "${RED}❌ Error HTTP $HTTP_CODE${NC}"
fi

# Test 3: Descargar Excel
echo ""
echo -e "${BLUE}[TEST 3]${NC} Descargando Excel de todas las ventas..."
EXCEL_OUTPUT="ventas_export.xlsx"
HTTP_CODE=$(curl -s -w "%{http_code}" -o "$EXCEL_OUTPUT" \
    -H "Accept: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" \
    "$BASE_URL/reportes/ventas/excel")

if [ "$HTTP_CODE" = "200" ]; then
    FILE_SIZE=$(ls -lh "$EXCEL_OUTPUT" | awk '{print $5}')
    echo -e "${GREEN}✅ Excel descargado: $EXCEL_OUTPUT ($FILE_SIZE)${NC}"
else
    echo -e "${RED}❌ Error HTTP $HTTP_CODE${NC}"
fi

# Test 4: Previsualizar PDF
echo ""
echo -e "${BLUE}[TEST 4]${NC} Verificando endpoint preview..."
HTTP_CODE=$(curl -s -w "%{http_code}" -o /dev/null \
    -H "Accept: application/pdf" \
    "$BASE_URL/reportes/venta/1/preview")

if [ "$HTTP_CODE" = "200" ]; then
    echo -e "${GREEN}✅ Preview disponible (HTTP $HTTP_CODE)${NC}"
else
    echo -e "${RED}❌ Error HTTP $HTTP_CODE${NC}"
fi

# Test 5: PDF por comportamiento
echo ""
echo -e "${BLUE}[TEST 5]${NC} Descargando PDF específico (FACTURA)..."
PDF_FACTURA="factura_especifico.pdf"
HTTP_CODE=$(curl -s -w "%{http_code}" -o "$PDF_FACTURA" \
    -H "Accept: application/pdf" \
    "$BASE_URL/reportes/venta/1/pdf/FACTURA")

if [ "$HTTP_CODE" = "200" ]; then
    FILE_SIZE=$(ls -lh "$PDF_FACTURA" | awk '{print $5}')
    echo -e "${GREEN}✅ PDF FACTURA descargado: $PDF_FACTURA ($FILE_SIZE)${NC}"
else
    echo -e "${RED}❌ Error HTTP $HTTP_CODE${NC}"
fi

# Test 6: Error handling - Venta inexistente
echo ""
echo -e "${BLUE}[TEST 6]${NC} Probando manejo de errores (Venta inexistente)..."
HTTP_CODE=$(curl -s -w "%{http_code}" -o /dev/null \
    "$BASE_URL/reportes/venta/9999/pdf")

if [ "$HTTP_CODE" = "404" ]; then
    echo -e "${GREEN}✅ Error 404 correcto${NC}"
else
    echo -e "${RED}❌ Error esperado 404, recibido: $HTTP_CODE${NC}"
fi

# Resumen
echo ""
echo "================================"
echo -e "${GREEN}✅ TESTING COMPLETADO${NC}"
echo "================================"
echo "Archivos generados:"
echo "  - $PDF_OUTPUT"
echo "  - $EXCEL_OUTPUT"
echo "  - $PDF_FACTURA"
echo ""
echo "Próximos pasos:"
echo "  1. Abrir archivos PDF con Adobe Reader o navegador"
echo "  2. Abrir Excel con Microsoft Excel o LibreOffice Calc"
echo "  3. Verificar que los datos sean correctos"
echo "  4. Revisar logs en consola IDE"
echo ""

