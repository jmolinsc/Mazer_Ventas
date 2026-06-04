package com.deyhayenterprise.mazeradmintemplate.web.controller;

import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.service.ReporteService;
import com.deyhayenterprise.mazeradmintemplate.service.VentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Controller para generación y descarga de reportes de ventas.
 * Soporta PDF, Excel y otros formatos.
 */
@Controller
@RequestMapping("/reportes")
@RequiredArgsConstructor
@Slf4j
public class ReporteController {

    private final ReporteService reporteService;
    private final VentaService ventaService;

    /**
     * Descarga el HTML de impresión de una venta específica
     * GET /reportes/venta/{id}/pdf
     */
    @GetMapping("/venta/{id}/pdf")
    @ResponseBody
    public ResponseEntity<byte[]> descargarPDFVenta(@PathVariable Long id) {
        try {
            Venta venta = ventaService.findById(id);
            ByteArrayOutputStream html = reporteService.generarPDF(venta);
            String nombreArchivo = reporteService.obtenerNombreArchivo(venta);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreArchivo + "\"");

            return new ResponseEntity<>(html.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error generando reporte para venta {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Preview de impresión (abre en nueva pestaña, Ctrl+P = PDF)
     * GET /reportes/venta/{id}/preview
     */
    @GetMapping("/venta/{id}/preview")
    @ResponseBody
    public ResponseEntity<byte[]> previewVenta(@PathVariable Long id) {
        return descargarPDFVenta(id);
    }

    /**
     * Exportar todas las ventas a CSV (compatible con Excel)
     * GET /reportes/ventas/excel
     */
    @GetMapping("/ventas/excel")
    @ResponseBody
    public ResponseEntity<byte[]> exportarExcel() {
        try {
            List<Venta> ventas = ventaService.findAll();
            ByteArrayOutputStream csv = reporteService.exportarVentasExcel(ventas);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"ventas.csv\"");

            return new ResponseEntity<>(csv.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error exportando CSV de ventas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * PDF por tipo de comportamiento
     * GET /reportes/venta/{id}/pdf/{tipo}
     */
    @GetMapping("/venta/{id}/pdf/{tipo}")
    @ResponseBody
    public ResponseEntity<byte[]> descargarPDFPorTipo(@PathVariable Long id,
                                                       @PathVariable String tipo) {
        try {
            Venta venta = ventaService.findById(id);
            ByteArrayOutputStream html = reporteService.generarPDFPorComportamiento(venta, tipo.toUpperCase());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.set(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + tipo.toUpperCase() + "-" + id + ".html\"");

            return new ResponseEntity<>(html.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error generando reporte por tipo {} para venta {}", tipo, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
