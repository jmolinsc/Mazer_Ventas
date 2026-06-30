package com.deyhayenterprise.mazeradmintemplate.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.service.reporte.ReporteFacturacionService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.service.ReporteService;
import com.deyhayenterprise.mazeradmintemplate.service.VentaService;
import com.deyhayenterprise.mazeradmintemplate.util.ReporteResponseUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador de Reportes con JasperReports 6.20.5
 * Genera PDFs, Excels y otros formatos a partir de templates JRXML
 */
@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Slf4j
public class ReporteController {

    private final ReporteService reporteService;
    private final VentaService ventaService;
    @Autowired
    private ReporteFacturacionService reporteFacturacionService;


    @GetMapping("/venta/imprimir/{id}")
    public void imprimirVenta(@PathVariable("id") Long id, HttpServletResponse response) {
        log.info("Imprimiendo venta con ID: {}", id);

        try {
            Venta venta = ventaService.findById(id);
            byte[] pdfBytes = reporteFacturacionService.procesarFactura(venta.getId());

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=factura_" + id + ".pdf");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();

        } catch (Exception e) {
            log.error("Error al generar factura", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/factura/{id}")
    public ResponseEntity<byte[]> imprimirFactura(@PathVariable Long id) {
        try {
            // Aquí debes obtener la venta de tu servicio
             Venta venta = ventaService.findById(id);

            // Temporal: crear un objeto simulado
            Object ventaSimulada = new Object();

            byte[] pdfBytes = reporteFacturacionService.procesarFactura(id);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "factura_" + id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/credito-fiscal/{id}")
    public ResponseEntity<byte[]> imprimirCreditoFiscal(@PathVariable Long id) {
        try {
            Object ventaSimulada = new Object();
            byte[] pdfBytes = reporteFacturacionService.procesarCreditoFiscal(ventaSimulada);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "credito_fiscal_" + id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    /**
     * Descargar PDF de una venta específica
     * @param id ID de la venta
     * @return ResponseEntity con archivo PDF
     */
    @GetMapping("/venta/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdfVenta(
            @PathVariable Long id) {
        try {
            log.info("Descargando PDF para venta ID: {}", id);
            Venta venta = ventaService.findById(id);
            if (venta == null) {
                log.warn("Venta no encontrada: {}", id);
                return ResponseEntity.notFound().build();
            }
            
            ByteArrayOutputStream pdf = reporteService.generarPDF(venta);
            String nombreArchivo = reporteService.obtenerNombreArchivo(venta);
            
            log.info("PDF generado correctamente: {} ({} bytes)", nombreArchivo, pdf.size());
            return ReporteResponseUtil.buildPdfResponse(pdf, nombreArchivo);
            
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
    public ResponseEntity<byte[]> previsualizarPdfVenta(
            @PathVariable Long id) {
        try {
            log.info("Previsualizando PDF para venta ID: {}", id);
            Venta venta = ventaService.findById(id);
            if (venta == null) {
                return ResponseEntity.notFound().build();
            }
            
            ByteArrayOutputStream pdf = reporteService.generarPDF(venta);
            String nombreArchivo = reporteService.obtenerNombreArchivo(venta);
            return ReporteResponseUtil.buildPdfInlineResponse(pdf, nombreArchivo);
            
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
            
            if (ventas == null || ventas.isEmpty()) {
                log.warn("No hay ventas para exportar");
                return ResponseEntity.noContent().build();
            }
            
            ByteArrayOutputStream excel = reporteService.exportarVentasExcel(ventas);
            String nombreArchivo = ReporteResponseUtil.generarNombreArchivoPorFecha("VENTAS") + ".xlsx";
            
            log.info("Excel generado: {} ({} ventas)", nombreArchivo, ventas.size());
            return ReporteResponseUtil.buildExcelResponse(excel, nombreArchivo);
            
        } catch (Exception ex) {
            log.error("Error al generar Excel: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Exportar ventas por rango de fechas
     * @param fechaInicio Fecha de inicio (yyyy-MM-dd)
     * @param fechaFin Fecha de fin (yyyy-MM-dd)
     * @return ResponseEntity con archivo Excel
     */
    @GetMapping("/ventas/excel/por-fecha")
    public ResponseEntity<byte[]> descargarExcelPorFecha(
            @RequestParam LocalDate fechaInicio,
            @RequestParam LocalDate fechaFin) {
        try {
            log.info("Descargando Excel de ventas entre {} y {}", fechaInicio, fechaFin);
            
            List<Venta> todasVentas = ventaService.findAll();
            if (todasVentas == null) {
                todasVentas = List.of();
            }
            
            List<Venta> ventasFiltradas = todasVentas.stream()
                    .filter(v -> v.getFecha() != null &&
                                 !v.getFecha().isBefore(fechaInicio) &&
                                 !v.getFecha().isAfter(fechaFin)
                    ).toList();
            
            if (ventasFiltradas.isEmpty()) {
                log.warn("No hay ventas en el rango {} - {}", fechaInicio, fechaFin);
                return ResponseEntity.noContent().build();
            }
            
            ByteArrayOutputStream excel = reporteService.exportarVentasExcel(ventasFiltradas);
            String nombreArchivo = "VENTAS-" + fechaInicio + "-" + fechaFin + ".xlsx";
            
            return ReporteResponseUtil.buildExcelResponse(excel, nombreArchivo);
            
        } catch (Exception ex) {
            log.error("Error al generar Excel por fechas", ex);
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
            if (venta == null) {
                return ResponseEntity.notFound().build();
            }
            
            ByteArrayOutputStream pdf = reporteService.generarPDFPorComportamiento(venta, comportamiento.toUpperCase());
            String nombreArchivo = comportamiento.toUpperCase() + "-" + venta.getId() + ".pdf";
            
            log.info("PDF {} generado: {}", comportamiento, nombreArchivo);
            return ReporteResponseUtil.buildPdfResponse(pdf, nombreArchivo);
            
        } catch (IllegalArgumentException ex) {
            log.error("Venta no encontrada: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception ex) {
            log.error("Error al generar PDF {} para venta {}: {}", comportamiento, id, ex.getMessage(), ex);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * Health check del servicio de reportes
     * @return Estado operativo del servicio
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("✅ Servicio de Reportes ACTIVO - JasperReports 6.20.5");
    }
}
