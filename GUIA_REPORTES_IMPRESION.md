# 📊 GUÍA DE REPORTES E IMPRESIÓN DE VENTAS

## ✨ Stack de Tecnologías Implementado

```
✅ Backend: Spring Boot 3.1.4 + Java 17
✅ PDF: iText 7 (librería estándar industrial)
✅ Excel: Apache POI 5.2.4
✅ Plantillas: Thymeleaf 3
✅ Formatos soportados: PDF, Excel
✅ Comportamientos: FACTURA, PEDIDO, DEVOLUCION, NOTA_CREDITO
```

---

## 🎯 Componentes Implementados

### 1. **ReporteServiceImpl** ✅ COMPLETADO
**Ruta:** `src/main/java/com/deyhayenterprise/mazeradmintemplate/service/impl/ReporteServiceImpl.java`

**Métodos principales:**
- `generarPDF(Venta venta)` → Genera PDF según comportamiento de venta
- `generarPDFPorComportamiento(Venta venta, String comportamiento)` → PDF personalizado
- `exportarVentasExcel(List<Venta> ventas)` → Excel con todas las ventas
- `obtenerNombreArchivo(Venta venta)` → Nombre sugerido del archivo

**Características:**
- ✅ Encabezado con logo "MAZER VENTAS"
- ✅ Datos generales (Movimiento, Folio, Fecha, Estado)
- ✅ Información del cliente (Nombre, NIT, Email, Teléfono)
- ✅ Tabla de detalles con productos (Item, Producto, Cantidad, Precio Unitario, Subtotal)
- ✅ Resumen financiero (Subtotal, Impuestos, Total)
- ✅ Pie de página con nota fiscal y texto según comportamiento
- ✅ Formato moneda automático ($X,XXX.XX)

---

### 2. **ReporteController** ✅ COMPLETADO NUEVO
**Ruta:** `src/main/java/com/deyhayenterprise/mazeradmintemplate/controller/ReporteController.java`

**Endpoints:**

| Método | Ruta | Descripción | Retorna |
|--------|------|-------------|---------|
| GET | `/reportes/venta/{id}/pdf` | Descarga PDF de venta | PDF (attachment) |
| GET | `/reportes/venta/{id}/preview` | Previsualiza PDF en navegador | PDF (inline) |
| GET | `/reportes/ventas/excel` | Descarga Excel con todas las ventas | Excel (.xlsx) |
| GET | `/reportes/venta/{id}/pdf/{comportamiento}` | PDF por comportamiento específico | PDF personalizado |

**Ejemplo de uso en cURL:**
```bash
# Descargar PDF
curl -o factura.pdf http://localhost:8080/reportes/venta/5/pdf

# Previsualizar en navegador
curl http://localhost:8080/reportes/venta/5/preview

# Descargar Excel
curl -o ventas.xlsx http://localhost:8080/reportes/ventas/excel
```

---

### 3. **Vistas Thymeleaf Actualizadas** ✅ COMPLETADO

#### **ventas/reportes.html** ✅ ACTUALIZADO
- Tabla de ventas con opciones de exportación
- Botones: **Ver PDF** (previsualización) y **Descargar PDF**
- Botón: **Exportar Excel** (todas las ventas)
- Instrucciones de uso para usuarios
- Diseño responsive con Bootstrap

#### **ventas/listar.html** ✅ MEJORADO
- Agregados botones en columna de acciones:
  - **Editar**: Modifica la venta
  - **Ver**: Previsualiza PDF
  - **PDF**: Descarga comprobante

---

## 🚀 Cómo Usar el Sistema

### **Opción 1: Desde la Vista "Reportes"**
```
1. Ir a: http://localhost:8080/ventas/reportes
2. Seleccionar venta deseada en la tabla
3. Opciones:
   - "Ver" → Abre PDF en nueva pestaña
   - "PDF" → Descarga archivo PDF
4. Para Excel: Presionar "Exportar Excel" arriba
```

### **Opción 2: Desde la Lista de Ventas**
```
1. Ir a: http://localhost:8080/ventas/listar
2. Buscar venta en la tabla
3. Presionar botón "PDF" en columna Acciones
4. Elegir:
   - "Ver" → Previsualización
   - "PDF" → Descargar
```

### **Opción 3: Desde la Edición de Venta**
```
1. Editar venta: http://localhost:8080/ventas/editar/{id}
2. Presionar botón "IMPRIMIR" en el formulario
3. Se abre PDF en nueva pestaña
```

---

## 📋 Datos Que Incluye el PDF

### **Encabezado**
- Logo/Nombre: "MAZER VENTAS"
- Tipo de documento: FACTURA / PEDIDO / DEVOLUCION / NOTA_CREDITO
- Línea separadora

### **Sección General**
```
Movimiento: CONTADO
Folio:      FACTURA-VTA-000001
Fecha:      15/03/2026 10:30
Estado:     CONCLUIDO
```

### **Datos del Cliente**
```
Nombre:     Juan López García
NIT/Cédula: 123456789
Email:      juan@email.com
Teléfono:   555-1234
```

### **Tabla de Productos**
```
Item | Producto          | Cantidad | Precio Unit. | Subtotal
-----|-------------------|----------|--------------|----------
1    | Laptop Dell XPS   | 2        | $800.00      | $1,600.00
2    | Mouse Logitech    | 5        | $25.00       | $125.00
```

### **Resumen Financiero**
```
Subtotal:           $1,725.00
Impuestos (0%):     $0.00
─────────────────────────────
TOTAL:              $1,725.00
```

### **Pie de Página**
Ejemplo para FACTURA:
```
Documento generado por MAZER VENTAS | 15/03/2026 10:32
Este es un documento fiscal válido para propósitos tributarios.
```

---

## 🔧 Configuración en VentaServiceImpl

El servicio de ventas ya incluye integración con ReporteService:

```java
// En VentaServiceImpl.java - Línea 46
private final ReporteService reporteService;

// El servicio está inyectado y listo para usar
```

---

## 🎨 Personalización de PDFs

### **Cambiar Logo/Nombre Empresa**
**Archivo:** `ReporteServiceImpl.java` - Línea 38
```java
private static final String LOGO_EMPRESA = "MAZER VENTAS";
// Cambiar a:
private static final String LOGO_EMPRESA = "MI EMPRESA S.A.";
```

### **Cambiar Formato de Fecha**
**Archivo:** `ReporteServiceImpl.java` - Línea 37
```java
private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
// Cambiar a:
private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");
```

### **Cambiar Símbolo de Moneda**
Buscar método `formatearMoneda()` en `ReporteServiceImpl.java` - Línea 290
```java
return String.format("$%,.2f", valor);
// Cambiar $ por €, Q, etc.
```

### **Agregar Logotipo de Imagen**
En `agregarEncabezado()` método:
```java
// Agregar después de Paragraph titulo:
Image logo = new Image(ImageDataFactory.create("path/to/logo.png"));
logo.setWidth(100);
document.add(logo);
```

---

## 📊 Comportamientos Soportados

| Comportamiento | Descripción | Texto Footer |
|---|---|---|
| **FACTURA** | Documento fiscal oficial | "Este es un documento fiscal válido para propósitos tributarios." |
| **PEDIDO** | Orden de compra/venta | "Esta es una orden de compra. Sujeta a confirmación y disponibilidad." |
| **DEVOLUCION** | Comprobante de devolución | "Comprobante de devolución. Aplica para cambios o reintegros." |
| **NOTA_CREDITO** | Ajuste contable | "Nota de crédito válida para ajustes contables." |

---

## ⚙️ Dependencias en pom.xml

```xml
<!-- PDF Generation (iText 7) -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext-core</artifactId>
    <version>8.0.3</version>
</dependency>

<!-- Excel Export (Apache POI) -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.4</version>
</dependency>

<!-- Jasper Reports (opcional para reportes avanzados) -->
<dependency>
    <groupId>net.sf.jasperreports</groupId>
    <artifactId>jasperreports</artifactId>
    <version>6.20.5</version>
</dependency>
```

---

## 🧪 Testing Local

### **Test 1: Generar PDF Simple**
```
1. GET http://localhost:8080/ventas/listar
2. Presionar botón "PDF" en cualquier venta
3. Verificar descarga de archivo PDF
4. Abrir con Adobe Reader o navegador
```

### **Test 2: Previsualizar en Navegador**
```
1. GET http://localhost:8080/ventas/reportes
2. Presionar "Ver" en cualquier venta
3. Verificar que PDF abre en nueva pestaña
4. Verificar que pueda cerrarse sin guardar
```

### **Test 3: Exportar Excel**
```
1. GET http://localhost:8080/ventas/reportes
2. Presionar "Exportar Excel"
3. Verificar descarga de .xlsx
4. Abrir con Excel/Calc
5. Verificar columnas: ID, Fecha, Cliente, Comportamiento, Estado, Total, Cantidad
```

### **Test 4: PDF por Comportamiento**
```
1. GET http://localhost:8080/reportes/venta/5/pdf/FACTURA
2. Verificar PDF con footer de FACTURA
3. GET http://localhost:8080/reportes/venta/5/pdf/PEDIDO
4. Verificar PDF con footer de PEDIDO
```

---

## 🐛 Troubleshooting

### **Error 404: Endpoint No Encontrado**
```
Verificar:
✅ Controlador en paquete: com.deyhayenterprise.mazeradmintemplate.controller
✅ Anotación @RequestMapping("/reportes")
✅ Métodos @GetMapping correctos
✅ Aplicación ejecutándose en puerto 8080
```

### **Error 500: Exception en Generación**
```
Verificar:
✅ Venta existe en base de datos
✅ Venta tiene cliente asignado
✅ Venta tiene al menos 1 detalle (producto)
✅ Revisar consola IDE para stack trace
```

### **PDF descargado pero vacío**
```
Verificar:
✅ Venta tiene detalles completos
✅ Producto tiene nombre
✅ Cliente tiene nombre
✅ Total > 0
```

### **Error al exportar Excel**
```
Verificar:
✅ Apache POI 5.2.4 en pom.xml
✅ Permisos de lectura/escritura
✅ Espacio en disco disponible
```

---

## 📈 Performance y Optimización

### **Caché Recomendado** (Opcional)
```java
@Cacheable("reportePDFs")
public ByteArrayOutputStream generarPDF(Venta venta) {
    // Método cachea resultado por 1 hora
}
```

### **Generación Asincrónica** (Opcional)
```java
@Async
public CompletableFuture<byte[]> generarPdfAsincrono(Venta venta) {
    ByteArrayOutputStream pdf = generarPDF(venta);
    return CompletableFuture.completedFuture(pdf.toByteArray());
}
```

---

## 🔐 Seguridad

### **Validar Permisos** (Ya Implementado)
En `VentasController.java`:
- `VENTAS_AFECTAR` → Permiso para afectar
- `VENTAS_CANCELAR` → Permiso para cancelar
- Los reportes están disponibles para todos los usuarios autenticados

### **Restricción Opcional de Acceso**
```java
@GetMapping("/venta/{id}/pdf")
public ResponseEntity<byte[]> descargarPdfVenta(
    @PathVariable Long id,
    @AuthenticationPrincipal UserDetails user) {
    
    // Validar que usuario tiene acceso a la venta
    if (!tieneAcceso(id, user)) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    // ... resto del código
}
```

---

## 📝 Logs Generados

El sistema registra:
```log
[INFO] Descargando PDF para venta ID: 5
[INFO] Previsualizando PDF para venta ID: 5
[INFO] Descargando Excel de todas las ventas
[ERROR] Venta no encontrada: 999
[ERROR] Error al generar PDF para venta 5: NullPointerException
```

---

## 🎓 Ejemplos de Código para Desarrolladores

### **Usar ReporteService Directamente**
```java
@Autowired
private ReporteService reporteService;

// En algún método:
Venta venta = ventaRepository.findById(5L).orElse(null);
ByteArrayOutputStream pdf = reporteService.generarPDF(venta);
byte[] pdfBytes = pdf.toByteArray();
```

### **Generar PDF e Incluir en Email**
```java
Venta venta = ventaService.findById(id);
ByteArrayOutputStream pdf = reporteService.generarPDF(venta);

MimeMessageHelper helper = new MimeMessageHelper(message, true);
helper.addAttachment(reporteService.obtenerNombreArchivo(venta), 
    new ByteArrayResource(pdf.toByteArray()), 
    "application/pdf");
```

### **Guardar PDF en Disco**
```java
ByteArrayOutputStream pdf = reporteService.generarPDF(venta);
Files.write(
    Paths.get("uploads/reportes/" + venta.getId() + ".pdf"),
    pdf.toByteArray(),
    StandardOpenOption.CREATE,
    StandardOpenOption.WRITE
);
```

---

## 📚 Documentación Externa

- [iText 7 Docs](https://kb.itextpdf.com/)
- [Apache POI Guide](https://poi.apache.org/components/spreadsheet/)
- [Spring Boot REST](https://spring.io/guides/gs/rest-service/)
- [Thymeleaf Integration](https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html)

---

## ✅ Checklist de Implementación Completada

- ✅ Servicio ReporteService implementado (iText + POI)
- ✅ Controlador ReporteController creado con 4 endpoints
- ✅ Vista reportes.html actualizada y funcional
- ✅ Vista listar.html mejorada con botones PDF
- ✅ Integración con VentaService y ReporteService
- ✅ Soporte para 4 comportamientos (FACTURA, PEDIDO, DEVOLUCION, NOTA_CREDITO)
- ✅ Generación de Excel con Apache POI
- ✅ Manejo de errores y logging
- ✅ Responsabilidad HTTP correctas
- ✅ Nombres de archivo dinámicos
- ✅ Documentación completa

---

## 🎯 Próximos Pasos (Opcionales)

1. **Agregar Firma Digital** a PDFs
2. **Integración con correo** para envío automático
3. **Reportes por rango de fechas** con filtrado
4. **Firma manuscrita** en PDFs de FACTURA
5. **QR o código de barras** en comprobantes
6. **Watermark** (marca de agua) en PDFs
7. **Compresión automática** de PDFs
8. **Generación de folios** correlativo automático

---

**¡Sistema de Reportes Completamente Implementado! 🚀**

Para soporte o consultas, revisar logs en consola IDE o contactar al equipo de desarrollo.

