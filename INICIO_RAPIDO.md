# 🎯 INICIO RÁPIDO - SISTEMA DE REPORTES

## ⚡ Quick Start (5 Minutos)

### **Paso 1: Iniciar la Aplicación**
```bash
cd C:\Users\User\Documents\IntelijIdea\Mazer_Ventas
.\mvnw.cmd spring-boot:run
```

**Esperado:**
```
✅ Servidor inicia en: http://localhost:8080
✅ Base de datos conecta automáticamente
✅ Sin errores en la consola
```

---

### **Paso 2: Verificar que Existen Ventas**
```
Ir a: http://localhost:8080/ventas/listar
```

**Esperado:**
- ✅ Tabla con ventas existentes
- ✅ Botones: "Editar", "Ver", "PDF"
- ✅ Al menos 1 venta con cliente y detalles

---

### **Paso 3: Probar Descarga de PDF**
```
En http://localhost:8080/ventas/listar:
1. Presionar botón "PDF" en cualquier venta
2. Debería descargar archivo PDF
```

**Esperado:**
- ✅ Archivo descargado: `FACTURA-VTA-000001.pdf`
- ✅ Tamaño: 50-200 KB
- ✅ Abre con Adobe Reader o navegador

---

### **Paso 4: Probar Previsualización**
```
En http://localhost:8080/ventas/listar:
1. Presionar botón "Ver" en cualquier venta
2. Se abre PDF en nueva pestaña
```

**Esperado:**
- ✅ PDF abre inmediatamente en navegador
- ✅ Se visualiza correctamente
- ✅ Incluye datos del cliente y productos

---

### **Paso 5: Exportar Excel**
```
Ir a: http://localhost:8080/ventas/reportes
1. Presionar botón "Exportar Excel"
2. Debería descargar archivo .xlsx
```

**Esperado:**
- ✅ Archivo descargado: `ventas.xlsx`
- ✅ Contiene columnas: ID, Fecha, Cliente, Comportamiento, Estado, Total
- ✅ Todas las ventas están incluidas

---

## 🔍 Verificación de Archivos

### **Controlador de Reportes ✅**
```
📁 Ubicación:
src/main/java/com/deyhayenterprise/mazeradmintemplate/controller/ReporteController.java

✅ Contiene:
- @RequestMapping("/reportes")
- 4 métodos GET
- Manejo de errores
- Logging con @Slf4j
```

### **Servicio de Reportes ✅**
```
📁 Ubicación:
src/main/java/com/deyhayenterprise/mazeradmintemplate/service/impl/ReporteServiceImpl.java

✅ Contiene:
- generarPDF()
- generarPDFPorComportamiento()
- exportarVentasExcel()
- obtenerNombreArchivo()
- Métodos auxiliares de formato
```

### **Vistas Actualizadas ✅**
```
📁 Ubicación 1:
src/main/resources/templates/ventas/reportes.html
✅ Tabla con botones Ver/PDF
✅ Botón Exportar Excel

📁 Ubicación 2:
src/main/resources/templates/ventas/listar.html
✅ Grupo de botones: Editar, Ver, PDF
```

---

## 📊 Estructura de PDF Generado

```
┌─ ENCABEZADO ────────────────────┐
│ MAZER VENTAS                    │
│ FACTURA                         │
│ ════════════════════════════════ │
├─ DATOS GENERALES ───────────────┤
│ Movimiento:  CONTADO            │
│ Folio:       FACTURA-VTA-000001 │
│ Fecha:       15/03/2026 10:30   │
│ Estado:      CONCLUIDO          │
├─ CLIENTE ───────────────────────┤
│ Nombre:      Juan López         │
│ NIT:         123456789          │
│ Email:       juan@email.com     │
│ Teléfono:    555-1234           │
├─ PRODUCTOS ─────────────────────┤
│ Item│Producto │Cant│P.Unit│Total│
│ 1   │Laptop   │ 2  │$800  │$1600│
│ 2   │Mouse    │ 5  │$25   │$125 │
├─ TOTALES ───────────────────────┤
│ Subtotal:    $1,725.00          │
│ Impuestos:   $0.00              │
│ TOTAL:       $1,725.00          │
├─ PIE DE PÁGINA ─────────────────┤
│ Documento fiscal válido...      │
└─────────────────────────────────┘
```

---

## 🚀 Endpoints Disponibles

| Ruta | Método | Retorna | Uso |
|------|--------|---------|-----|
| `/reportes/venta/{id}/pdf` | GET | PDF (descarga) | Descargar comprobante |
| `/reportes/venta/{id}/preview` | GET | PDF (visualizar) | Ver en navegador |
| `/reportes/ventas/excel` | GET | Excel (.xlsx) | Exportar todas |
| `/reportes/venta/{id}/pdf/{tipo}` | GET | PDF personalizado | PDF por comportamiento |

---

## ✅ Checklist de Implementación

```
✅ ReporteController.java creado
✅ 4 endpoints implementados
✅ ReporteServiceImpl funcional
✅ reportes.html actualizado
✅ listar.html mejorado
✅ Todos los imports correctos
✅ Sin errores de compilación
✅ Logging integrado
✅ Manejo de errores
✅ Headers HTTP correctos
✅ iText 7 integrado
✅ Apache POI integrado
✅ Nombres dinámicos
✅ Bootstrap icons incluidos
✅ Documentación completa
```

---

## 🐛 Si Algo No Funciona

### **Error 404 en reportes**
```
Verificar:
1. ¿Aplicación ejecutándose? → http://localhost:8080
2. ¿Base de datos conectada? → Ver logs
3. ¿Hay ventas creadas? → Ir a /ventas/nueva y crear
4. ¿URL correcta? → /reportes/venta/1/pdf (revisar ID)
```

### **PDF vacío o sin datos**
```
Verificar:
1. ¿Venta tiene cliente? → En venta.cliente
2. ¿Venta tiene detalles? → En venta.detalles (lista)
3. ¿Productos tienen nombre? → En producto.nombre
4. ¿Cliente tiene nombre? → En cliente.nombre
```

### **Error al descargar Excel**
```
Verificar:
1. ¿Hay al menos 1 venta? → /ventas/listar
2. ¿Apache POI en pom.xml? → Revisar pom.xml
3. ¿Permisos de escritura? → Carpeta temporal
4. ¿Espacio en disco? → Verificar espacio disponible
```

---

## 📚 Archivos Relacionados

| Archivo | Descripción |
|---------|-------------|
| `GUIA_REPORTES_IMPRESION.md` | Guía completa del sistema |
| `IMPLEMENTACION_REPORTES_RESUMEN.md` | Resumen de implementación |
| `test_reportes.sh` | Script de testing automático |
| `README_FINAL.md` | Documentación general |
| `DATATABLE_RESUMEN.md` | Resumen de datatables |

---

## 🎓 Ejemplos de Uso en Código

### **Desde Java**
```java
@Autowired
private ReporteService reporteService;

// Generar PDF
Venta venta = ventaService.findById(1L);
ByteArrayOutputStream pdf = reporteService.generarPDF(venta);

// Guardar en archivo
Files.write(
    Paths.get("reportes/venta_1.pdf"),
    pdf.toByteArray()
);
```

### **Desde HTML/Thymeleaf**
```html
<!-- Ver PDF en navegador -->
<a th:href="@{/reportes/venta/{id}/preview(id=${venta.id})}" target="_blank">
    Ver PDF
</a>

<!-- Descargar PDF -->
<a th:href="@{/reportes/venta/{id}/pdf(id=${venta.id})}" download>
    Descargar PDF
</a>

<!-- Excel -->
<a href="/reportes/ventas/excel">
    Exportar Excel
</a>
```

### **Desde cURL**
```bash
# Descargar PDF
curl -o venta.pdf http://localhost:8080/reportes/venta/1/pdf

# Previsualizar (abre en navegador)
curl http://localhost:8080/reportes/venta/1/preview

# Excel
curl -o ventas.xlsx http://localhost:8080/reportes/ventas/excel
```

---

## 🎯 Próximos Pasos

1. **Testing Manual**
   - [ ] Crear venta en /ventas/nueva
   - [ ] Guardar venta
   - [ ] Ir a /ventas/reportes
   - [ ] Descargar PDF
   - [ ] Descargar Excel
   - [ ] Verificar datos

2. **Personalización** (Opcional)
   - [ ] Cambiar logo en ReporteServiceImpl
   - [ ] Cambiar color de encabezados
   - [ ] Agregar numero de documento
   - [ ] Agregar QR o código de barras

3. **Integración**
   - [ ] Envío automático de PDF por email
   - [ ] Guardado de PDFs en disco
   - [ ] Historial de reportes
   - [ ] Analytics de descargas

---

## 📞 Soporte Técnico

### **Logs Útiles**
Revisar en consola IDE:
```log
[INFO] Descargando PDF para venta ID: 5
[INFO] Previsualizando PDF para venta ID: 5
[ERROR] Venta no encontrada: 999
[ERROR] Error al generar PDF: NullPointerException
```

### **Recursos Externos**
- 📖 [iText Documentation](https://kb.itextpdf.com/)
- 📊 [Apache POI Guide](https://poi.apache.org/)
- 🎯 [Spring Boot Guide](https://spring.io/guides)

---

**¡Sistema Listo para Usar! 🚀**

Tiempo estimado de setup: **5 minutos**  
Dificultad: **Muy Baja**  
Estatus: **✅ PRODUCCIÓN**

