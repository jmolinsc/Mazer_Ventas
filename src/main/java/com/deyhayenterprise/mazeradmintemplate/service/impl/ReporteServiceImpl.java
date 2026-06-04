package com.deyhayenterprise.mazeradmintemplate.service.impl;

import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.entity.VentaDetalle;
import com.deyhayenterprise.mazeradmintemplate.service.ReporteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Implementación de ReporteService SIN dependencias externas.
 *
 * • generarPDF / generarPDFPorComportamiento → HTML print-ready (abre en browser, Ctrl+P = PDF)
 * • exportarVentasExcel                       → CSV compatible con Excel
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteServiceImpl implements ReporteService {

    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ──────────────────────────────────────────────────────────────
    // PDF (HTML para imprimir)
    // ──────────────────────────────────────────────────────────────

    @Override
    public ByteArrayOutputStream generarPDF(Venta venta) {
        if (venta == null) throw new IllegalArgumentException("Venta no puede ser nula");
        String comp = StringUtils.hasText(venta.getComportamiento())
                ? venta.getComportamiento().trim().toUpperCase(Locale.ROOT)
                : "FACTURA";
        return generarPDFPorComportamiento(venta, comp);
    }

    @Override
    public ByteArrayOutputStream generarPDFPorComportamiento(Venta venta, String comportamiento) {
        String html = buildHtml(venta, comportamiento);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.writeBytes(html.getBytes(StandardCharsets.UTF_8));
        log.info("Reporte HTML generado para venta {}", venta.getId());
        return baos;
    }

    // ──────────────────────────────────────────────────────────────
    // Excel → CSV
    // ──────────────────────────────────────────────────────────────

    @Override
    public ByteArrayOutputStream exportarVentasExcel(List<Venta> ventas) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Fecha,Cliente,Comportamiento,Estado,Total,Cantidad\n");
        for (Venta v : ventas) {
            csv.append(nvl(v.getId())).append(",");
            csv.append(v.getFecha() != null ? v.getFecha().format(FMT_DATE) : "").append(",");
            csv.append(escape(v.getCliente() != null ? v.getCliente().getNombre() : "")).append(",");
            csv.append(nvl(v.getComportamiento())).append(",");
            csv.append(nvl(v.getEstado())).append(",");
            csv.append(v.getTotal() != null ? v.getTotal().toPlainString() : "0").append(",");
            csv.append(nvl(v.getCantidad())).append("\n");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.writeBytes(csv.toString().getBytes(StandardCharsets.UTF_8));
        return baos;
    }

    @Override
    public String obtenerNombreArchivo(Venta venta) {
        if (venta == null) return "venta.html";
        String comp = StringUtils.hasText(venta.getComportamiento())
                ? venta.getComportamiento().trim().toUpperCase(Locale.ROOT)
                : "VENTA";
        String folio = venta.getMovid() != null
                ? venta.getMovid()
                : String.format("%06d", venta.getId() != null ? venta.getId() : 0);
        return comp + "-" + folio + ".html";
    }

    // ──────────────────────────────────────────────────────────────
    // Construcción del HTML
    // ──────────────────────────────────────────────────────────────

    private String buildHtml(Venta venta, String comp) {
        String cliente      = venta.getCliente() != null ? esc(venta.getCliente().getNombre()) : "N/A";
        String nit          = venta.getCliente() != null ? esc(venta.getCliente().getNit())     : "N/A";
        String email        = venta.getCliente() != null ? esc(venta.getCliente().getEmail())   : "N/A";
        String telefono     = venta.getCliente() != null ? esc(venta.getCliente().getTelefono()): "N/A";
        String mov          = esc(nvl(venta.getMov()));
        String folio        = venta.getMovid() != null ? esc(venta.getMovid()) : "SIN AFECTAR";
        String fecha        = venta.getFecha()  != null ? venta.getFecha().format(FMT_DATE) : "N/A";
        String estado       = esc(nvl(venta.getEstado()));
        String totalStr     = fmt(venta.getTotal());
        String timestamp    = LocalDateTime.now().format(FMT_DATE_TIME);
        String nota         = notaFinal(comp);

        // Filas de detalle
        StringBuilder filas = new StringBuilder();
        int idx = 1;
        if (venta.getDetalles() != null) {
            for (VentaDetalle d : venta.getDetalles()) {
                String prod = d.getProducto() != null ? esc(d.getProducto().getNombre()) : "N/A";
                filas.append("<tr>")
                     .append("<td>").append(idx++).append("</td>")
                     .append("<td>").append(prod).append("</td>")
                     .append("<td class='center'>").append(d.getCantidad()).append("</td>")
                     .append("<td class='right'>").append(fmt(d.getPrecioUnitario())).append("</td>")
                     .append("<td class='right'>").append(fmt(d.getSubtotal())).append("</td>")
                     .append("</tr>\n");
            }
        }

        return "<!DOCTYPE html>\n<html lang='es'>\n<head>\n"
            + "<meta charset='UTF-8'>\n"
            + "<title>" + comp + " - " + folio + "</title>\n"
            + "<style>\n"
            + "  * { margin:0; padding:0; box-sizing:border-box; }\n"
            + "  body { font-family: Arial, sans-serif; font-size: 12px; color: #222; padding: 30px; }\n"
            + "  h1 { text-align:center; font-size:22px; margin-bottom:4px; }\n"
            + "  h2 { text-align:center; font-size:14px; color:#435EBE; margin-bottom:8px; }\n"
            + "  hr  { border:none; border-top:2px solid #435EBE; margin:10px 0; }\n"
            + "  .grid { display:grid; grid-template-columns:1fr 1fr; gap:4px; margin-bottom:10px; }\n"
            + "  .label { background:#EBEDF5; font-weight:bold; padding:4px 8px; border:1px solid #CCC; }\n"
            + "  .value { padding:4px 8px; border:1px solid #CCC; }\n"
            + "  h3 { font-size:11px; font-weight:bold; margin:10px 0 4px; }\n"
            + "  table { width:100%; border-collapse:collapse; margin-bottom:10px; }\n"
            + "  thead tr { background:#435EBE; color:#fff; }\n"
            + "  th, td { padding:5px 8px; border:1px solid #D2D2D2; }\n"
            + "  .center { text-align:center; }\n"
            + "  .right  { text-align:right; }\n"
            + "  .totales { margin-left:auto; width:260px; }\n"
            + "  .totales td { border:1px solid #CCC; padding:4px 8px; }\n"
            + "  .total-row { background:#DCE6FF; font-weight:bold; font-size:13px; }\n"
            + "  .footer { margin-top:14px; text-align:center; font-size:10px; color:#666; font-style:italic; }\n"
            + "  @media print {\n"
            + "    body { padding:10px; }\n"
            + "    .no-print { display:none; }\n"
            + "    @page { size: A4; margin: 15mm; }\n"
            + "  }\n"
            + "  .btn-print { display:block; margin:0 auto 20px; padding:8px 24px; background:#435EBE;"
            + "               color:#fff; border:none; border-radius:4px; cursor:pointer; font-size:14px; }\n"
            + "</style>\n</head>\n<body>\n"
            + "<button class='btn-print no-print' onclick='window.print()'>🖨 Imprimir / Guardar PDF</button>\n"
            + "<h1>MAZER VENTAS</h1>\n"
            + "<h2>" + comp + "</h2>\n"
            + "<hr/>\n"
            + "<div class='grid'>\n"
            + "  <div class='label'>Movimiento:</div><div class='value'>" + mov + "</div>\n"
            + "  <div class='label'>Folio:</div><div class='value'>" + folio + "</div>\n"
            + "  <div class='label'>Fecha:</div><div class='value'>" + fecha + "</div>\n"
            + "  <div class='label'>Estado:</div><div class='value'>" + estado + "</div>\n"
            + "</div>\n"
            + "<h3>DATOS DEL CLIENTE</h3>\n"
            + "<div class='grid'>\n"
            + "  <div class='label'>Nombre:</div><div class='value'>" + cliente + "</div>\n"
            + "  <div class='label'>NIT/Cédula:</div><div class='value'>" + nit + "</div>\n"
            + "  <div class='label'>Email:</div><div class='value'>" + email + "</div>\n"
            + "  <div class='label'>Teléfono:</div><div class='value'>" + telefono + "</div>\n"
            + "</div>\n"
            + "<h3>DETALLE DE PRODUCTOS</h3>\n"
            + "<table>\n"
            + "  <thead><tr><th>#</th><th>Producto</th><th>Cantidad</th><th>Precio Unit.</th><th>Subtotal</th></tr></thead>\n"
            + "  <tbody>\n" + filas + "  </tbody>\n"
            + "</table>\n"
            + "<table class='totales'>\n"
            + "  <tr><td class='right'>Subtotal:</td><td class='right'>" + totalStr + "</td></tr>\n"
            + "  <tr><td class='right'>Impuestos (0%):</td><td class='right'>$0.00</td></tr>\n"
            + "  <tr class='total-row'><td class='right'>TOTAL:</td><td class='right'>" + totalStr + "</td></tr>\n"
            + "</table>\n"
            + "<hr/>\n"
            + "<div class='footer'>Generado: " + timestamp + " &nbsp;|&nbsp; " + nota + "</div>\n"
            + "</body>\n</html>";
    }

    // ──────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────

    private String notaFinal(String comp) {
        return switch (comp) {
            case "FACTURA"      -> "Documento fiscal válido para propósitos tributarios.";
            case "PEDIDO"       -> "Orden de compra. Sujeta a confirmación y disponibilidad.";
            case "DEVOLUCION"   -> "Comprobante de devolución. Aplica para cambios o reintegros.";
            case "NOTA_CREDITO" -> "Nota de crédito válida para ajustes contables.";
            default             -> "Documento generado por el sistema MAZER VENTAS.";
        };
    }

    private String fmt(BigDecimal v) {
        return v != null ? String.format("$%,.2f", v) : "$0.00";
    }

    /** Escapa caracteres HTML para prevenir XSS. */
    private String esc(String v) {
        if (v == null) return "N/A";
        return v.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }

    private String nvl(Object v) {
        return v != null ? v.toString() : "";
    }

    /** Escapa valores para CSV. */
    private String escape(String v) {
        if (v == null) return "";
        if (v.contains(",") || v.contains("\"") || v.contains("\n")) {
            return "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }
}
