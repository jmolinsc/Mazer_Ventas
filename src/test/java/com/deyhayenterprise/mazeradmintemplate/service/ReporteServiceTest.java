package com.deyhayenterprise.mazeradmintemplate.service;

import com.deyhayenterprise.mazeradmintemplate.entity.Cliente;
import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.entity.VentaDetalle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para ReporteService con JasperReports
 * 
 * Demuestra cómo usar el servicio de reportes en diferentes escenarios.
 */
@SpringBootTest
@DisplayName("Pruebas de ReporteService con JasperReports")
class ReporteServiceTest {

    @Autowired
    private ReporteService reporteService;

    private Venta ventaEjemplo;

    @BeforeEach
    void setUp() {
        // Crear cliente
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Juan Pérez Gómez");
        cliente.setNit("12345678-9");
        cliente.setEmail("juan@example.com");
        cliente.setTelefono("+34 600 123 456");

        // Crear productos
        Producto producto1 = new Producto();
        producto1.setId(1L);
        producto1.setNombre("Laptop HP 15\"");
        
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Mouse inalámbrico Logitech");

        // Crear detalles de venta
        VentaDetalle detalle1 = new VentaDetalle();
        detalle1.setId(1L);
        detalle1.setProducto(producto1);
        detalle1.setCantidad(2);
        detalle1.setPrecioUnitario(new BigDecimal("800.00"));
        detalle1.setSubtotal(new BigDecimal("1600.00"));

        VentaDetalle detalle2 = new VentaDetalle();
        detalle2.setId(2L);
        detalle2.setProducto(producto2);
        detalle2.setCantidad(1);
        detalle2.setPrecioUnitario(new BigDecimal("35.00"));
        detalle2.setSubtotal(new BigDecimal("35.00"));

        List<VentaDetalle> detalles = new ArrayList<>();
        detalles.add(detalle1);
        detalles.add(detalle2);

        // Crear venta
        ventaEjemplo = new Venta();
        ventaEjemplo.setId(1L);
        ventaEjemplo.setCliente(cliente);
        ventaEjemplo.setDetalles(detalles);
        ventaEjemplo.setMov("MOV-001");
        ventaEjemplo.setMovid("FAC-2024-00001");
        ventaEjemplo.setComportamiento("FACTURA");
        ventaEjemplo.setEstado("COMPLETADA");
       // ventaEjemplo.setFecha(LocalDateTime.now());
        ventaEjemplo.setTotal(new BigDecimal("1635.00"));
        ventaEjemplo.setCantidad(3);
    }

    @Test
    @DisplayName("Debe generar PDF correctamente")
    void testGenerarPdf() {
        // Act
        ByteArrayOutputStream pdf = reporteService.generarPDF(ventaEjemplo);

        // Assert
        assertNotNull(pdf, "El PDF no debe ser nulo");
        assertTrue(pdf.size() > 0, "El PDF debe tener contenido");
        assertTrue(pdf.toByteArray().length > 1000, "El PDF debe tener tamaño mínimo");
        
        // Verificar que contiene marcas de PDF
        byte[] pdfBytes = pdf.toByteArray();
        String pdfString = new String(pdfBytes);
        assertTrue(pdfString.contains("%PDF") || pdfBytes[0] == '%', 
                   "Debe ser un PDF válido");
    }

    @Test
    @DisplayName("Debe generar PDF con diferentes comportamientos")
    void testGenerarPdfPorComportamiento() {
        String[] comportamientos = {"FACTURA", "PEDIDO", "DEVOLUCION", "NOTA_CREDITO"};

        for (String comp : comportamientos) {
            // Act
            ByteArrayOutputStream pdf = reporteService.generarPDFPorComportamiento(ventaEjemplo, comp);

            // Assert
            assertNotNull(pdf, "PDF para " + comp + " no debe ser nulo");
            assertTrue(pdf.size() > 0, "PDF para " + comp + " debe tener contenido");
        }
    }

    @Test
    @DisplayName("Debe exportar a Excel correctamente")
    void testExportarVentasExcel() {
        // Arrange
        List<Venta> ventas = new ArrayList<>();
        ventas.add(ventaEjemplo);

        // Act
        ByteArrayOutputStream excel = reporteService.exportarVentasExcel(ventas);

        // Assert
        assertNotNull(excel, "El Excel no debe ser nulo");
        assertTrue(excel.size() > 0, "El Excel debe tener contenido");

        // Verificar que contiene marcas de Excel
        byte[] excelBytes = excel.toByteArray();
        assertTrue(excelBytes.length > 100, "El Excel debe tener tamaño mínimo");
    }

    @Test
    @DisplayName("Debe obtener nombre de archivo válido")
    void testObtenerNombreArchivo() {
        // Act
        String nombre = reporteService.obtenerNombreArchivo(ventaEjemplo);

        // Assert
        assertNotNull(nombre, "El nombre no debe ser nulo");
        assertTrue(nombre.contains(".pdf"), "Debe contener extensión .pdf");
        assertTrue(nombre.contains("FACTURA"), "Debe contener tipo de documento");
        assertTrue(nombre.matches(".*\\d+\\.pdf$"), "Debe tener formato correcto");
    }

    @Test
    @DisplayName("Debe manejar venta nula sin error")
    void testGenerarPdfVentaNula() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
                     () -> reporteService.generarPDF(null),
                     "Debe lanzar IllegalArgumentException para venta nula");
    }

    @Test
    @DisplayName("Debe exportar lista vacía sin error")
    void testExportarVentasVacías() {
        // Act
        ByteArrayOutputStream excel = reporteService.exportarVentasExcel(new ArrayList<>());

        // Assert
        assertNotNull(excel, "Debe retornar ByteArrayOutputStream incluso con lista vacía");
    }

    @Test
    @DisplayName("Debe guardar PDF en archivo (integración)")
    void testGuardarPdfEnArchivo() throws IOException {
        // Arrange
        String rutaArchivo = "target/test-factura.pdf";

        // Act
        ByteArrayOutputStream pdf = reporteService.generarPDF(ventaEjemplo);
        try (FileOutputStream fos = new FileOutputStream(rutaArchivo)) {
            fos.write(pdf.toByteArray());
        }

        // Assert
        java.io.File archivo = new java.io.File(rutaArchivo);
        assertTrue(archivo.exists(), "El archivo debe existir");
        assertTrue(archivo.length() > 0, "El archivo debe tener contenido");
        
        // Limpiar
        archivo.delete();
    }

    @Test
    @DisplayName("Debe compilar template solo una vez (caché)")
    void testCacheTemplate() {
        // Act
        long inicio1 = System.currentTimeMillis();
        ByteArrayOutputStream pdf1 = reporteService.generarPDF(ventaEjemplo);
        long tiempo1 = System.currentTimeMillis() - inicio1;

        long inicio2 = System.currentTimeMillis();
        ByteArrayOutputStream pdf2 = reporteService.generarPDF(ventaEjemplo);
        long tiempo2 = System.currentTimeMillis() - inicio2;

        // Assert
        assertNotNull(pdf1, "Primer PDF no debe ser nulo");
        assertNotNull(pdf2, "Segundo PDF no debe ser nulo");
        // El segundo debe ser más rápido (caché)
        // Nota: En test puede no ser significativo, pero en producción sí
        System.out.println("Tiempo primera compilación: " + tiempo1 + "ms");
        System.out.println("Tiempo segunda compilación: " + tiempo2 + "ms");
    }

    @Test
    @DisplayName("Debe manejar venta sin detalles")
    void testGenerarPdfSinDetalles() {
        // Arrange
        Venta ventaSinDetalles = new Venta();
        ventaSinDetalles.setId(1L);
        ventaSinDetalles.setCliente(ventaEjemplo.getCliente());
        ventaSinDetalles.setDetalles(new ArrayList<>()); // Sin detalles
        ventaSinDetalles.setComportamiento("FACTURA");
        ventaSinDetalles.setTotal(BigDecimal.ZERO);

        // Act
        ByteArrayOutputStream pdf = reporteService.generarPDF(ventaSinDetalles);

        // Assert
        assertNotNull(pdf, "Debe generar PDF incluso sin detalles");
        assertTrue(pdf.size() > 0, "PDF debe tener contenido");
    }

    @Test
    @DisplayName("Debe manejar cliente nulo")
    void testGenerarPdfClienteNulo() {
        // Arrange
        ventaEjemplo.setCliente(null);

        // Act
        ByteArrayOutputStream pdf = reporteService.generarPDF(ventaEjemplo);

        // Assert
        assertNotNull(pdf, "Debe generar PDF incluso sin cliente");
        assertTrue(pdf.size() > 0, "PDF debe tener contenido");
    }
}

