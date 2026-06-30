# ✅ IMPLEMENTACIÓN JASPERREPORTS - RESUMEN TÉCNICO

## 📦 Lo que se ha implementado

### 1. **Dependencias Maven** (pom.xml)
```xml
✅ JasperReports 6.20.5
✅ Apache POI 5.2.4 (para Excel)
```

### 2. **Estructura de carpetas**
```
src/main/resources/reports/
└── venta.jrxml              ✅ Template profesional de factura/venta
```

### 3. **Servicio de Reportes** (ReporteServiceImpl.java)
```java
✅ generarPDF(Venta)                          // PDF con datos de venta
✅ generarPDFPorComportamiento(Venta, String) // PDF con tipo específico
✅ exportarVentasExcel(List<Venta>)          // Excel con múltiples ventas
✅ obtenerNombreArchivo(Venta)               // Nombre recomendado

Características:
- Compilación automática de template JRXML
- Caché en memoria para evitar recompilaciones
- Parametrización flexible
- Manejo de datasources con JRBeanCollectionDataSource
- POJO MapDetalle para mapeo de campos
```

### 4. **Controlador REST** (ReporteController.java)
```
GET /reportes/venta/{id}/pdf                      ✅ Descargar PDF
GET /reportes/venta/{id}/preview                  ✅ Ver PDF en línea
GET /reportes/venta/{id}/pdf/{comportamiento}     ✅ PDF con tipo específico
GET /reportes/ventas/excel                        ✅ Excel de todas las ventas
GET /reportes/ventas/excel/por-fecha              ✅ Excel por rango de fechas
GET /reportes/health                              ✅ Health check
```

### 5. **Utilidades** 
```java
✅ ReporteResponseUtil.java
   - buildPdfResponse()        // Response HTTP para PDF descarga
   - buildExcelResponse()      // Response HTTP para Excel descarga
   - buildPdfInlineResponse()  // Response HTTP para PDF inline (visualizar)
   - buildCsvResponse()        // Response HTTP para CSV
   - sanitizarNombre()         // Validar nombres de archivo
   - generarNombreArchivoPorFecha() // Nombre automático con fecha

✅ ReporteConfigDTO.java
   - DTO para configuración flexible de reportes
   - Soporta opciones como marca de agua, email, etc.
```

### 6. **Template JRXML** (venta.jrxml)
```
PARÁMETROS DISPONIBLES:
- COMP              // Tipo de documento (FACTURA, PEDIDO, etc.)
- MOV, FOLIO        // Números de referencia
- FECHA, ESTADO     // Datos temporales y estado
- CLI_*             // Datos del cliente (nombre, NIT, email, teléfono)
- SUBTOTAL, TOTAL   // Montos
- TIMESTAMP, NOTA   // Metadata y pie de página

FIELDS (para iteración de detalle):
- item              // Número de item
- producto          // Nombre del producto
- cantidad          // Cantidad vendida
- precioUnitario    // Precio unitario
- subtotalDet       // Subtotal del item

BANDAS:
- Title             // Encabezado empresa, tipo doc, datos cliente
- ColumnHeader      // Títulos de columnas
- Detail            // Filas de productos (repite por cada item)
- Summary           // Totales y pie de página
```

---

## 🚀 Cómo usar

### Opción 1: Desde el controlador (REST API)
```bash
# Descargar PDF de factura
GET http://localhost:8080/reportes/venta/1/pdf

# Ver PDF en línea
GET http://localhost:8080/reportes/venta/1/preview

# Descargar como pedido
GET http://localhost:8080/reportes/venta/1/pdf/PEDIDO

# Exportar todas las ventas a Excel
GET http://localhost:8080/reportes/ventas/excel

# Exportar ventas de un período
GET http://localhost:8080/reportes/ventas/excel/por-fecha?fechaInicio=2024-01-01&fechaFin=2024-06-30
```

### Opción 2: Desde un servicio Java
```java
@Autowired
private ReporteService reporteService;

// Generar PDF
Venta venta = ventaService.findById(1L);
ByteArrayOutputStream pdf = reporteService.generarPDF(venta);

// Guardar en archivo
try (FileOutputStream fos = new FileOutputStream("factura.pdf")) {
    fos.write(pdf.toByteArray());
}

// O enviar por HTTP
return ReporteResponseUtil.buildPdfResponse(pdf, "factura.pdf");
```

### Opción 3: Desde JavaScript/Frontend
```javascript
// Descargar PDF
function descargarPDF(ventaId) {
    fetch(`/reportes/venta/${ventaId}/pdf`)
        .then(response => response.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `FACTURA-${ventaId}.pdf`;
            a.click();
            window.URL.revokeObjectURL(url);
        });
}

// Ver en línea
function verPDF(ventaId) {
    window.open(`/reportes/venta/${ventaId}/preview`, '_blank');
}
```

---

## 🎨 Personalización del Template

### Para modificar el diseño:
1. Abrir `src/main/resources/reports/venta.jrxml` en un editor XML
2. O usar **JasperStudio** (IDE específico) para edición visual
   - Descargar en: https://community.jaspersoft.com/project/jasperreports-studio

### Parámetros en el template:
```xml
<!-- Parámetros (se pasan desde Java) -->
<parameter name="COMP" class="java.lang.String" 
           defaultValueExpression="&quot;FACTURA&quot;"/>

<!-- Campos (vienen del datasource) -->
<field name="producto" class="java.lang.String"/>

<!-- Usar parámetro -->
<textFieldExpression><![CDATA[$P{COMP}]]></textFieldExpression>

<!-- Usar campo -->
<textFieldExpression><![CDATA[$F{producto}]]></textFieldExpression>
```

---

## 📁 Crear nuevos templates

### Estructura básica:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports"
              name="nuevoReporte" pageWidth="595" pageHeight="842"
              columnWidth="523" leftMargin="36" rightMargin="36"
              topMargin="36" bottomMargin="36">

    <parameter name="TITULO" class="java.lang.String"/>
    <field name="valor" class="java.lang.String"/>
    
    <title>
        <band height="50">
            <textField>
                <reportElement x="0" y="0" width="523" height="50"/>
                <textFieldExpression><![CDATA[$P{TITULO}]]></textFieldExpression>
            </textField>
        </band>
    </title>
    
    <detail>
        <band height="20">
            <textField>
                <reportElement x="0" y="0" width="523" height="20"/>
                <textFieldExpression><![CDATA[$F{valor}]]></textFieldExpression>
            </textField>
        </band>
    </detail>
</jasperReport>
```

### Registro en ReporteServiceImpl:
```java
private JasperReport reporteComplementarioCache;

private JasperReport getCompiledReporteComplementario() throws JRException {
    if (reporteComplementarioCache == null) {
        try (InputStream is = getClass()
                .getResourceAsStream("/reports/nuevoReporte.jrxml")) {
            reporteComplementarioCache = JasperCompileManager.compileReport(is);
        } catch (IOException e) {
            throw new JRException("Error al compilar", e);
        }
    }
    return reporteComplementarioCache;
}
```

---

## 📊 Flujo de ejecución

```
Usuario solicita PDF
        ↓
ReporteController.descargarPdfVenta(id)
        ↓
VentaService.findById(id) → Venta entity
        ↓
ReporteService.generarPDF(venta)
        ↓
1. Obtener template compilado (con caché)
2. Construir mapa de PARÁMETROS desde Venta
3. Construir DATASOURCE desde VentaDetalle[]
4. JasperFillManager.fillReport() → JasperPrint
5. JasperExportManager.exportReportToPdfStream()
6. ByteArrayOutputStream con PDF
        ↓
ReporteResponseUtil.buildPdfResponse()
        ↓
Response HTTP con headers MIME correcto
        ↓
Navegador: descarga o visualiza PDF
```

---

## 🐛 Troubleshooting

### "No se encontró el template /reports/venta.jrxml"
```
✓ Verificar que el archivo existe en:
  src/main/resources/reports/venta.jrxml

✓ Ejecutar: mvn clean compile
```

### "Field 'producto' not found"
```
✓ Asegurar que MapDetalle tiene getters:
  public String getProducto() { return producto; }

✓ Verificar que los names en JRXML coinciden con getters
```

### "La venta está nula en el reporte"
```
✓ Revisar que buildParams() completa todos los parámetros
✓ Ejecutar: log.info("Params: {}", params);
✓ Asegurar que la venta tiene detalles (no lista vacía)
```

### "Error de compilación del JRXML"
```
✓ Validar XML: debe estar bien formado
✓ Usar JasperStudio para edición visual
✓ Revisar logs: "Error JasperReports al generar PDF"
```

---

## 📚 Referencias

- **JasperReports Docs**: https://community.jaspersoft.com/documentation
- **JRXML Schema**: https://jasperreports.sourceforge.net/xsd/
- **JasperStudio IDE**: https://community.jaspersoft.com/project/jasperreports-studio
- **Apache POI Docs**: https://poi.apache.org/

---

## ✨ Características implementadas

- ✅ Generación PDF con JasperReports 6.20.5
- ✅ Compilación automática de templates JRXML
- ✅ Caché en memoria para performance
- ✅ Parametrización flexible
- ✅ Datasources con JRBeanCollectionDataSource
- ✅ Exportación Excel con Apache POI
- ✅ Endpoints REST documentados con OpenAPI/Swagger
- ✅ Manejo seguro de nombres de archivo
- ✅ Respuestas HTTP con headers correctos
- ✅ Logging completo
- ✅ Utilidades para construcción de responses
- ✅ DTO para configuración flexible

---

**Estado**: ✅ COMPLETAMENTE IMPLEMENTADO  
**Versión**: 1.0  
**Fecha**: 2024-06-15  
**JasperReports**: 6.20.5  
**Apache POI**: 5.2.4

