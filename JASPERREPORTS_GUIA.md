# 📊 IMPLEMENTACIÓN JASPERREPORTS - GUÍA COMPLETA

## 📋 Contenido
- [Introducción](#introducción)
- [Estructura de carpetas](#estructura-de-carpetas)
- [Dependencias](#dependencias)
- [Estructura del template JRXML](#estructura-del-template-jrxml)
- [Métodos del servicio](#métodos-del-servicio)
- [Uso desde el controlador](#uso-desde-el-controlador)
- [Parámetros disponibles](#parámetros-disponibles)
- [Ejemplos de uso](#ejemplos-de-uso)
- [Troubleshooting](#troubleshooting)

---

## ✨ Introducción

JasperReports es un motor de reportes basado en Java que permite generar documentos profesionales en PDF, Excel, CSV y más. La implementación actual está configurada para:

- **Versión**: JasperReports 6.20.5
- **Compilación**: Automática del template `.jrxml` en tiempo de ejecución
- **Caché**: El template compilado se guarda en memoria para evitar recompilaciones innecesarias
- **Formato primario**: PDF profesional con estilos y branding

---

## 📁 Estructura de carpetas

```
src/main/resources/reports/
├── venta.jrxml                 # Template principal de factura/venta
└── [otros templates aquí]
```

**Importante**: Los archivos `.jrxml` deben estar en la ruta exacta `src/main/resources/reports/` para que sean detectados en tiempo de ejecución.

---

## 🔧 Dependencias

### En pom.xml agregamos:

```xml
<!-- JasperReports -->
<dependency>
    <groupId>net.sf.jasperreports</groupId>
    <artifactId>jasperreports</artifactId>
    <version>6.20.5</version>
</dependency>

<!-- Apache POI para Excel -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.4</version>
</dependency>
```

### Instalación (si es necesario):
```bash
mvn dependency:resolve
```

---

## 🎨 Estructura del template JRXML

El archivo `venta.jrxml` tiene la siguiente estructura:

### 1. **Declaración XML**
```xml
<?xml version="1.0" encoding="UTF-8"?>
<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports"
              name="venta" pageWidth="595" pageHeight="842">
```

### 2. **PARÁMETROS** (valores pasados desde Java)
```xml
<parameter name="COMP" class="java.lang.String" defaultValueExpression="&quot;FACTURA&quot;"/>
<parameter name="MOV" class="java.lang.String"/>
<parameter name="FOLIO" class="java.lang.String"/>
<parameter name="FECHA" class="java.lang.String"/>
<!-- ... más parámetros ... -->
```

### 3. **FIELDS** (campos del datasource de detalle)
```xml
<field name="item" class="java.lang.Integer"/>
<field name="producto" class="java.lang.String"/>
<field name="cantidad" class="java.lang.Integer"/>
<field name="precioUnitario" class="java.lang.String"/>
<field name="subtotalDet" class="java.lang.String"/>
```

### 4. **BANDAS** (secciones del reporte)
- **Title**: Encabezado (logo, datos generales, cliente)
- **ColumnHeader**: Títulos de columnas
- **Detail**: Filas de productos (se repite por cada item)
- **Summary**: Totales y pie

---

## 🚀 Métodos del servicio

### ReporteServiceImpl.java

#### 1. **generarPDF(Venta venta)**
```java
public ByteArrayOutputStream generarPDF(Venta venta)
```
- Genera PDF para una venta
- Usa el comportamiento de la venta para los títulos
- **Retorna**: ByteArrayOutputStream con el PDF
- **Lanza**: RuntimeException si hay error

#### 2. **generarPDFPorComportamiento(Venta venta, String comportamiento)**
```java
public ByteArrayOutputStream generarPDFPorComportamiento(Venta venta, String comportamiento)
```
- Genera PDF especificando el tipo de documento
- Valores válidos: "FACTURA", "PEDIDO", "DEVOLUCION", "NOTA_CREDITO"
- Cambia el pie de página según el comportamiento

#### 3. **exportarVentasExcel(List<Venta> ventas)**
```java
public ByteArrayOutputStream exportarVentasExcel(List<Venta> ventas)
```
- Exporta múltiples ventas a Excel
- Genera una hoja con columnas: ID, Fecha, Cliente, Comportamiento, Estado, Total, Cantidad
- **Retorna**: ByteArrayOutputStream con el XLSX

#### 4. **obtenerNombreArchivo(Venta venta)**
```java
public String obtenerNombreArchivo(Venta venta)
```
- Genera el nombre de archivo recomendado
- Formato: `FACTURA-000001.pdf`
- Usa el folio o ID de la venta

---

## 🎯 Uso desde el controlador

### ReporteController.java

#### Endpoint: Descargar PDF
```java
@GetMapping("/reportes/venta/{id}/pdf")
public ResponseEntity<byte[]> descargarPDF(@PathVariable Long id) {
    Venta venta = ventaService.obtenerPorId(id);
    ByteArrayOutputStream baos = reporteService.generarPDF(venta);
    
    return ResponseEntity.ok()
        .header("Content-Disposition", 
                "attachment; filename=\"" + reporteService.obtenerNombreArchivo(venta) + "\"")
        .contentType(MediaType.APPLICATION_PDF)
        .body(baos.toByteArray());
}
```

#### Endpoint: PDF con comportamiento específico
```java
@GetMapping("/reportes/venta/{id}/pdf/{comportamiento}")
public ResponseEntity<byte[]> descargarPDFPorComportamiento(
        @PathVariable Long id,
        @PathVariable String comportamiento) {
    Venta venta = ventaService.obtenerPorId(id);
    ByteArrayOutputStream baos = reporteService.generarPDFPorComportamiento(venta, comportamiento);
    
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=\"" + comportamiento + "-" + id + ".pdf\"")
        .contentType(MediaType.APPLICATION_PDF)
        .body(baos.toByteArray());
}
```

#### Endpoint: Excel con múltiples ventas
```java
@GetMapping("/reportes/ventas/excel")
public ResponseEntity<byte[]> exportarVentasExcel() {
    List<Venta> ventas = ventaService.obtenerTodas();
    ByteArrayOutputStream baos = reporteService.exportarVentasExcel(ventas);
    
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=\"Ventas-" + LocalDate.now() + ".xlsx\"")
        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(baos.toByteArray());
}
```

---

## 📊 Parámetros disponibles

El template `venta.jrxml` acepta los siguientes parámetros:

| Parámetro | Tipo | Descripción | Ejemplo |
|-----------|------|-------------|---------|
| `COMP` | String | Tipo de documento | "FACTURA", "PEDIDO" |
| `MOV` | String | Número de movimiento | "MOV-001" |
| `FOLIO` | String | Folio del documento | "FAC-2024-00001" |
| `FECHA` | String | Fecha formateada | "15/06/2024" |
| `ESTADO` | String | Estado de la venta | "COMPLETADA", "PENDIENTE" |
| `CLI_NOMBRE` | String | Nombre del cliente | "Juan Pérez" |
| `CLI_NIT` | String | NIT/Cédula del cliente | "12345678-9" |
| `CLI_EMAIL` | String | Email del cliente | "juan@example.com" |
| `CLI_TEL` | String | Teléfono del cliente | "+34 600 123 456" |
| `SUBTOTAL` | String | Subtotal formateado | "$1,000.00" |
| `TOTAL` | String | Total formateado | "$1,000.00" |
| `TIMESTAMP` | String | Fecha/hora de generación | "Generado: 15/06/2024 14:30" |
| `NOTA` | String | Nota al pie | "Documento fiscal válido..." |

---

## 💡 Ejemplos de uso

### Ejemplo 1: Generar PDF desde controller
```java
@GetMapping("/descargar-factura/{id}")
public ResponseEntity<byte[]> descargarFactura(@PathVariable Long id) {
    // Obtener la venta
    Venta venta = ventaService.obtenerPorId(id);
    if (venta == null) {
        return ResponseEntity.notFound().build();
    }
    
    // Generar PDF
    ByteArrayOutputStream pdf = reporteService.generarPDF(venta);
    
    // Descargar
    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_PDF)
        .header("Content-Disposition", 
                "attachment; filename=\"" + reporteService.obtenerNombreArchivo(venta) + "\"")
        .body(pdf.toByteArray());
}
```

### Ejemplo 2: Desde frontend (JavaScript)
```javascript
function descargarPDF(ventaId) {
    fetch(`/api/reportes/venta/${ventaId}/pdf`)
        .then(response => response.blob())
        .then(blob => {
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `FACTURA-${ventaId}.pdf`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
        });
}
```

### Ejemplo 3: Enviar por email (con JavaMail)
```java
@PostMapping("/enviar-factura/{id}")
public ResponseEntity<?> enviarFacturaPorEmail(@PathVariable Long id) {
    Venta venta = ventaService.obtenerPorId(id);
    ByteArrayOutputStream pdf = reporteService.generarPDF(venta);
    
    // Crear email con attachment
    SimpleMailMessage email = new SimpleMailMessage();
    email.setTo(venta.getCliente().getEmail());
    email.setSubject("Su Factura: " + reporteService.obtenerNombreArchivo(venta));
    email.setText("Adjunto encontrará su factura...");
    
    // Enviar con attachment (requiere MimeMessageHelper)
    // ... código de envío ...
    
    return ResponseEntity.ok("Email enviado");
}
```

---

## 🔍 Troubleshooting

### Error: "No se encontró el template /reports/venta.jrxml"

**Causa**: El archivo no está en la ubicación correcta.

**Solución**:
```bash
# Verificar que existe
ls -la src/main/resources/reports/
# Debe mostrar: venta.jrxml

# Compilar en Maven
mvn clean compile
```

---

### Error: "Field 'producto' not found in datasource"

**Causa**: El POJO `MapDetalle` no tiene getters para los fields definidos.

**Solución**: Asegurar que `MapDetalle` tiene todos los getters:
```java
public Integer getItem() { return item; }
public String getProducto() { return producto; }
public Integer getCantidad() { return cantidad; }
public String getPrecioUnitario() { return precioUnitario; }
public String getSubtotalDet() { return subtotalDet; }
```

---

### Error: "JasperReport compilation failed"

**Causa**: Sintaxis inválida en el `.jrxml` (XML malformado).

**Solución**:
1. Abrir el archivo en un editor XML
2. Validar que todos los tags estén cerrados correctamente
3. Usar JasperStudio (IDE específico para JRXML) para editar

---

### El PDF se genera vacío o sin datos

**Causa**: Los parámetros no están siendo pasados correctamente.

**Solución**:
1. Verificar que `buildParams()` está completando todos los parámetros
2. Revisar logs: `log.info("Parámetros: {}", params);`
3. Asegurar que la `List<VentaDetalle>` no está vacía

---

## 📝 Crear nuevo template JRXML

### Estructura básica de un nuevo template:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<jasperReport xmlns="http://jasperreports.sourceforge.net/jasperreports"
              name="miReporte" pageWidth="595" pageHeight="842"
              columnWidth="523" leftMargin="36" rightMargin="36"
              topMargin="36" bottomMargin="36">

    <!-- Parámetros -->
    <parameter name="TITULO" class="java.lang.String" defaultValueExpression="&quot;Mi Reporte&quot;"/>
    
    <!-- Fields para datos -->
    <field name="campo1" class="java.lang.String"/>
    <field name="campo2" class="java.lang.Integer"/>
    
    <!-- Banda de título -->
    <title>
        <band height="50">
            <textField>
                <reportElement x="0" y="0" width="523" height="50"/>
                <textElement textAlignment="Center">
                    <font size="18" isBold="true"/>
                </textElement>
                <textFieldExpression><![CDATA[$P{TITULO}]]></textFieldExpression>
            </textField>
        </band>
    </title>
    
    <!-- Cabecera de columnas -->
    <columnHeader>
        <band height="20">
            <staticText>
                <reportElement x="0" y="0" width="260" height="20" backcolor="#435EBE" forecolor="#FFFFFF"/>
                <textElement textAlignment="Center" verticalAlignment="Middle">
                    <font isBold="true"/>
                </textElement>
                <text><![CDATA[Campo 1]]></text>
            </staticText>
            <staticText>
                <reportElement x="260" y="0" width="263" height="20" backcolor="#435EBE" forecolor="#FFFFFF"/>
                <textElement textAlignment="Center" verticalAlignment="Middle">
                    <font isBold="true"/>
                </textElement>
                <text><![CDATA[Campo 2]]></text>
            </staticText>
        </band>
    </columnHeader>
    
    <!-- Detalle (se repite) -->
    <detail>
        <band height="18">
            <textField>
                <reportElement x="0" y="0" width="260" height="18"/>
                <textElement verticalAlignment="Middle"/>
                <textFieldExpression><![CDATA[$F{campo1}]]></textFieldExpression>
            </textField>
            <textField>
                <reportElement x="260" y="0" width="263" height="18"/>
                <textElement textAlignment="Right" verticalAlignment="Middle"/>
                <textFieldExpression><![CDATA[$F{campo2}]]></textFieldExpression>
            </textField>
        </band>
    </detail>
</jasperReport>
```

---

## 🎓 Recursos

- **JasperReports Docs**: https://community.jaspersoft.com/documentation
- **JRXML Syntax**: https://jasperreports.sourceforge.net/xsd/
- **JasperStudio IDE**: https://community.jaspersoft.com/project/jasperreports-studio

---

**Última actualización**: 2024-06-15  
**Versión**: 1.0  
**Estado**: ✅ IMPLEMENTADO

