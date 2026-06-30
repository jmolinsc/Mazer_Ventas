# 🚀 JASPERREPORTS - INICIO RÁPIDO

## ⚡ 5 minutos para empezar

### 1️⃣ Compilar el proyecto
```bash
cd C:\Users\User\Documents\IntelijIdea\Mazer_Ventas
mvn clean compile
```

### 2️⃣ Iniciar la aplicación
```bash
mvn spring-boot:run
```

### 3️⃣ Verificar que funciona
```bash
# Health check
curl http://localhost:8080/reportes/health

# Respuesta:
# ✅ Servicio de Reportes ACTIVO - JasperReports 6.20.5
```

### 4️⃣ Descargar tu primer PDF
```bash
# Descargar factura de venta ID=1
curl -o factura.pdf http://localhost:8080/reportes/venta/1/pdf

# Abrir en tu navegador
http://localhost:8080/reportes/venta/1/preview
```

### 5️⃣ Exportar a Excel
```bash
# Exportar todas las ventas
curl -o ventas.xlsx http://localhost:8080/reportes/ventas/excel
```

---

## 🎯 Endpoints principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/reportes/venta/{id}/pdf` | Descargar PDF |
| GET | `/reportes/venta/{id}/preview` | Ver PDF en línea |
| GET | `/reportes/venta/{id}/pdf/{comportamiento}` | PDF tipo específico |
| GET | `/reportes/ventas/excel` | Excel de todas las ventas |
| GET | `/reportes/ventas/excel/por-fecha?fechaInicio=...&fechaFin=...` | Excel por período |
| GET | `/reportes/health` | Estado del servicio |

---

## 📂 Estructura de archivos

```
src/main/
├── java/com/deyhayenterprise/mazeradmintemplate/
│   ├── controller/
│   │   └── ReporteController.java          ✅ REST endpoints
│   ├── service/
│   │   ├── ReporteService.java            (interfaz)
│   │   └── impl/
│   │       └── ReporteServiceImpl.java     ✅ Lógica con JasperReports
│   ├── dto/
│   │   └── ReporteConfigDTO.java          ✅ Configuración flexible
│   └── util/
│       └── ReporteResponseUtil.java       ✅ Construcción de respuestas HTTP
│
└── resources/
    └── reports/
        └── venta.jrxml                    ✅ Template profesional

pom.xml
├── net.sf.jasperreports:jasperreports:6.20.5
└── org.apache.poi:poi-ooxml:5.2.4
```

---

## 💡 Ejemplos de uso

### Desde JavaScript
```html
<!DOCTYPE html>
<html>
<head>
    <title>Reportes</title>
</head>
<body>
    <button onclick="descargarPDF(1)">📥 Descargar PDF</button>
    <button onclick="verPDF(1)">👁️ Ver PDF</button>
    <button onclick="descargarExcel()">📊 Exportar Excel</button>

    <script>
        function descargarPDF(ventaId) {
            fetch(`/reportes/venta/${ventaId}/pdf`)
                .then(response => response.blob())
                .then(blob => {
                    const url = URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = `FACTURA-${ventaId}.pdf`;
                    a.click();
                });
        }

        function verPDF(ventaId) {
            window.open(`/reportes/venta/${ventaId}/preview`, '_blank');
        }

        function descargarExcel() {
            fetch('/reportes/ventas/excel')
                .then(response => response.blob())
                .then(blob => {
                    const url = URL.createObjectURL(blob);
                    const a = document.createElement('a');
                    a.href = url;
                    a.download = 'ventas.xlsx';
                    a.click();
                });
        }
    </script>
</body>
</html>
```

### Desde Java/Spring
```java
@Autowired
private ReporteService reporteService;

@Autowired
private VentaService ventaService;

@GetMapping("/mi-reporte/{id}")
public ResponseEntity<byte[]> miReporte(@PathVariable Long id) {
    Venta venta = ventaService.findById(id);
    ByteArrayOutputStream pdf = reporteService.generarPDF(venta);
    return ReporteResponseUtil.buildPdfResponse(pdf, "mi-factura.pdf");
}
```

---

## 🎨 Personalizar el template

### Opción A: Editor XML
1. Abrir: `src/main/resources/reports/venta.jrxml`
2. Editar directamente el XML
3. Compilar: `mvn clean compile`

### Opción B: JasperStudio (recomendado)
1. Descargar: https://community.jaspersoft.com/project/jasperreports-studio
2. Abrir archivo JRXML
3. Edición visual con preview
4. Guardar automáticamente

### Cambios comunes:
- Modificar colores: `backcolor="#435EBE"` → `backcolor="#FF0000"`
- Cambiar fuente: `<font size="18" isBold="true"/>`
- Agregar logo: `<image>` tag
- Editar textos: `<text><![CDATA[...]]></text>`

---

## 🔧 Crear nuevo template

### 1. Crear archivo JRXML
```bash
cp src/main/resources/reports/venta.jrxml src/main/resources/reports/pedido.jrxml
```

### 2. Editar nuevo template
- Cambiar nombre: `<jasperReport name="pedido">`
- Ajustar parámetros y fields
- Personalizar diseño

### 3. Usar en ReporteServiceImpl
```java
private JasperReport pedidoReportCache;

public ByteArrayOutputStream generarPedido(Venta venta) {
    JasperReport report = getCompiledPedidoReport();
    // ... resto del código
}

private JasperReport getCompiledPedidoReport() throws JRException {
    if (pedidoReportCache == null) {
        try (InputStream is = getClass()
                .getResourceAsStream("/reports/pedido.jrxml")) {
            pedidoReportCache = JasperCompileManager.compileReport(is);
        } catch (IOException e) {
            throw new JRException("Error", e);
        }
    }
    return pedidoReportCache;
}
```

---

## 🐛 Troubleshooting rápido

### ❌ "No se encontró el template"
```bash
✓ mvn clean compile
✓ Verificar: src/main/resources/reports/venta.jrxml existe
✓ Reiniciar aplicación
```

### ❌ "Error JasperReports"
```bash
✓ Revisar logs: 
  grep -i "jasper" logs/app.log
✓ Validar XML en venta.jrxml
✓ Ejecutar mvn clean compile
```

### ❌ "Field not found"
```bash
✓ Verificar que MapDetalle tiene getters para todos los fields
✓ Nombres coinciden: <field name="producto"/> ↔ getProducto()
```

### ❌ "PDF vacío"
```bash
✓ Asegurar que Venta.detalles no está vacío
✓ Verificar que buildParams() completa todos los parámetros
✓ Agregar logs: log.info("Detalles: {}", venta.getDetalles());
```

---

## 📊 Tipos de documento soportados

| Tipo | Endpoint | Nota |
|------|----------|------|
| FACTURA | `/venta/{id}/pdf/FACTURA` | Documento fiscal |
| PEDIDO | `/venta/{id}/pdf/PEDIDO` | Orden de compra |
| DEVOLUCION | `/venta/{id}/pdf/DEVOLUCION` | Comprobante devolución |
| NOTA_CREDITO | `/venta/{id}/pdf/NOTA_CREDITO` | Ajuste contable |

Cada uno cambia el pie de página y título del reporte automáticamente.

---

## 📚 Documentación completa

Para más detalles, consulta:
- 📖 `JASPERREPORTS_GUIA.md` - Guía completa
- 📋 `JASPERREPORTS_IMPLEMENTACION.md` - Detalles técnicos

---

## ✅ Checklist de configuración

- ✅ Maven instalado
- ✅ Java 11+ configurado
- ✅ pom.xml actualizado con JasperReports
- ✅ Template `venta.jrxml` en `src/main/resources/reports/`
- ✅ ReporteServiceImpl implementado
- ✅ ReporteController registrado como @RestController
- ✅ Swagger/OpenAPI disponible (opcional)

---

## 🎉 ¡Listo!

Tu sistema de reportes con JasperReports está completamente operativo.

**Próximos pasos:**
1. Personalizar el template según tu marca
2. Agregar más tipos de reportes
3. Integrar con sistema de email para envíos automáticos
4. Agregar Dashboard de reportes disponibles

---

**Versión**: 1.0  
**Estado**: ✅ LISTO PARA PRODUCCIÓN  
**Última actualización**: 2024-06-15

