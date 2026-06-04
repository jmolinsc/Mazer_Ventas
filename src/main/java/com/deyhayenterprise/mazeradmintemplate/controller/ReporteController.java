package com.deyhayenterprise.mazeradmintemplate.controller;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.service.ReporteService;
import com.deyhayenterprise.mazeradmintemplate.service.VentaService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador legacy de reportes.
 * Nota: se mantiene solo como referencia histórica; NO se registra como bean.
 */
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Slf4j
public class ReporteController {

    private final ReporteService reporteService;
    private final VentaService ventaService;

    /**
     * Descarga PDF de una venta específica
     * @param id ID de la venta
     * @return ResponseEntity con archivo PDF
     */
    @GetMapping("/venta/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdfVenta(@PathVariable Long id) {
        try {
            log.info("Descargando PDF para venta ID: {}", id);
            Venta venta = ventaService.findById(id);
            ByteArrayOutputStream pdf = reporteService.generarPDF(venta);
            String nombreArchivo = reporteService.obtenerNombreArchivo(venta);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf.toByteArray());
        } catch (IllegalArgumentException ex) {
            log.error("Venta no encontrada: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            log.error("Error al generar PDF para venta {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Previsualiza PDF de una venta en el navegador
     * @param id ID de la venta
     * @return ResponseEntity con archivo PDF para visualizar
     */
    @GetMapping("/venta/{id}/preview")
    public ResponseEntity<byte[]> previsualizarPdfVenta(@PathVariable Long id) {
        try {
            log.info("Previsualizando PDF para venta ID: {}", id);
            Venta venta = ventaService.findById(id);
            ByteArrayOutputStream pdf = reporteService.generarPDF(venta);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf.toByteArray());
        } catch (IllegalArgumentException ex) {
            log.error("Venta no encontrada: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            log.error("Error al generar PDF para venta {}: {}", id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Descarga Excel con todas las ventas
     * @return ResponseEntity con archivo Excel
     */
    @GetMapping("/ventas/excel")
    public ResponseEntity<byte[]> descargarExcelVentas() {
        try {
            log.info("Descargando Excel de todas las ventas");
            List<Venta> ventas = ventaService.findAll();
            ByteArrayOutputStream excel = reporteService.exportarVentasExcel(ventas);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ventas.xlsx\"")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(excel.toByteArray());
        } catch (Exception ex) {
            log.error("Error al generar Excel: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Descarga PDF por comportamiento específico (FACTURA, PEDIDO, etc.)
     * @param id ID de la venta
     * @param comportamiento Tipo de documento
     * @return ResponseEntity con archivo PDF
     */
    @GetMapping("/venta/{id}/pdf/{comportamiento}")
    public ResponseEntity<byte[]> descargarPdfPorComportamiento(
            @PathVariable Long id,
            @PathVariable String comportamiento) {
        try {
            log.info("Descargando PDF {} para venta ID: {}", comportamiento, id);
            Venta venta = ventaService.findById(id);
            ByteArrayOutputStream pdf = reporteService.generarPDFPorComportamiento(venta, comportamiento);
            String nombreArchivo = comportamiento.toUpperCase() + "-" + venta.getId() + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreArchivo + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf.toByteArray());
        } catch (IllegalArgumentException ex) {
            log.error("Venta no encontrada: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            log.error("Error al generar PDF {} para venta {}: {}", comportamiento, id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }
}
