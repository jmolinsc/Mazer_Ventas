package com.deyhayenterprise.mazeradmintemplate.service.impl;

import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.entity.VentaDetalle;
import com.deyhayenterprise.mazeradmintemplate.service.ReporteService;
import com.deyhayenterprise.mazeradmintemplate.service.reporte.ReporteFacturacionService;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * Implementación de ReporteService usando OpenPDF (com.github.librepdf:openpdf)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReporteServiceImpl implements ReporteService {

    @Autowired
    private ReporteFacturacionService reporteFacturacionService;
    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DATE_TIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final Color COLOR_HEADER = new Color(67, 97, 238);
    private static final Color COLOR_HEADER_TEXT = Color.WHITE;
    private static final Color COLOR_ROW_ALT = new Color(245, 247, 255);

    // ─────────────────────────────────────────────
    // PDF
    // ─────────────────────────────────────────────

    @Override
    public ByteArrayOutputStream generarPDF(Venta venta) {
        if (venta == null) throw new IllegalArgumentException("Venta no puede ser nula");
        String comp = StringUtils.hasText(venta.getComportamiento())
                ? venta.getComportamiento().trim().toUpperCase(Locale.ROOT) : "FACTURA";
        return generarPDFPorComportamiento(venta, comp);
    }

    @Override
    public ByteArrayOutputStream generarPDFPorComportamiento(Venta venta, String comportamiento) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36, 36, 50, 36);
        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font fontTitulo  = new Font(Font.HELVETICA, 20, Font.BOLD, COLOR_HEADER);
            Font fontSubtitulo = new Font(Font.HELVETICA, 13, Font.BOLD, COLOR_HEADER);
            Font fontLabel   = new Font(Font.HELVETICA, 8, Font.BOLD);
            Font fontValue   = new Font(Font.HELVETICA, 8, Font.NORMAL);
            Font fontTableH  = new Font(Font.HELVETICA, 9, Font.BOLD, COLOR_HEADER_TEXT);
            Font fontTableR  = new Font(Font.HELVETICA, 9, Font.NORMAL);
            Font fontTotal   = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font fontNota    = new Font(Font.HELVETICA, 8, Font.ITALIC, Color.GRAY);

            // ── Encabezado ──
            Paragraph empresa = new Paragraph("MAZER VENTAS", fontTitulo);
            empresa.setAlignment(Element.ALIGN_CENTER);
            doc.add(empresa);

            Paragraph tipo = new Paragraph(comportamiento, fontSubtitulo);
            tipo.setAlignment(Element.ALIGN_CENTER);
            tipo.setSpacingAfter(6);
            doc.add(tipo);

            // Línea separadora
            doc.add(new Chunk(new com.lowagie.text.pdf.draw.LineSeparator(1.5f, 100, COLOR_HEADER, Element.ALIGN_CENTER, -2)));
            doc.add(Chunk.NEWLINE);

            // ── Datos del comprobante ──
            PdfPTable infoTable = new PdfPTable(4);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new float[]{2f, 3f, 2f, 3f});
            infoTable.setSpacingAfter(8);

            addInfoCell(infoTable, "Movimiento:", venta.getMov(), fontLabel, fontValue);
            addInfoCell(infoTable, "Folio:", venta.getMovid() != null ? venta.getMovid() : "SIN AFECTAR", fontLabel, fontValue);
            addInfoCell(infoTable, "Fecha:", venta.getFecha() != null ? venta.getFecha().format(FMT_DATE) : "N/A", fontLabel, fontValue);
            addInfoCell(infoTable, "Estado:", nvl(venta.getEstado()), fontLabel, fontValue);
            doc.add(infoTable);

            // ── Datos del cliente ──
            if (venta.getCliente() != null) {
                PdfPTable cliTable = new PdfPTable(4);
                cliTable.setWidthPercentage(100);
                cliTable.setWidths(new float[]{2f, 3f, 2f, 3f});
                cliTable.setSpacingAfter(10);

                addInfoCell(cliTable, "Cliente:", nvl(venta.getCliente().getNombre()), fontLabel, fontValue);
                addInfoCell(cliTable, "NIT:", nvl(venta.getCliente().getNit()), fontLabel, fontValue);
                addInfoCell(cliTable, "Email:", nvl(venta.getCliente().getEmail()), fontLabel, fontValue);
                addInfoCell(cliTable, "Teléfono:", nvl(venta.getCliente().getTelefono()), fontLabel, fontValue);
                doc.add(cliTable);
            }

            // ── Tabla de detalle ──
            PdfPTable detalle = new PdfPTable(5);
            detalle.setWidthPercentage(100);
            detalle.setWidths(new float[]{0.7f, 4f, 1.2f, 2f, 2f});
            detalle.setSpacingAfter(8);

            String[] headers = {"#", "Producto", "Cantidad", "Precio Unit.", "Subtotal"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, fontTableH));
                cell.setBackgroundColor(COLOR_HEADER);
                cell.setPadding(5);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorderColor(Color.WHITE);
                detalle.addCell(cell);
            }

            List<VentaDetalle> detalles = venta.getDetalles();
            int idx = 1;
            if (detalles != null) {
                for (VentaDetalle d : detalles) {
                    Color rowBg = (idx % 2 == 0) ? COLOR_ROW_ALT : Color.WHITE;
                    addDetalleRow(detalle, idx,
                            d.getProducto() != null ? d.getProducto().getNombre() : "N/A",
                            d.getCantidad(),
                            d.getPrecioUnitario(),
                            d.getSubtotal(),
                            fontTableR, rowBg);
                    idx++;
                }
            }
            doc.add(detalle);

            // ── Totales ──
            PdfPTable totales = new PdfPTable(2);
            totales.setWidthPercentage(40);
            totales.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totales.setSpacingAfter(12);

            addTotalRow(totales, "Subtotal:", fmt(venta.getTotal()), fontTotal);
            addTotalRow(totales, "TOTAL:", fmt(venta.getTotal()), fontTotal);
            doc.add(totales);

            // ── Nota al pie ──
            Paragraph nota = new Paragraph(notaFinal(comportamiento), fontNota);
            nota.setAlignment(Element.ALIGN_CENTER);
            nota.setSpacingBefore(10);
            doc.add(nota);

            doc.close();
            log.info("PDF generado exitosamente para venta {}", venta.getId());
        } catch (DocumentException e) {
            log.error("Error al generar PDF para venta {}", venta.getId(), e);
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
        return baos;
    }

    // ─────────────────────────────────────────────
    // Excel
    // ─────────────────────────────────────────────

    @Override
    public ByteArrayOutputStream exportarVentasExcel(List<Venta> ventas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Ventas");
            String[] headers = {"ID", "Fecha", "Cliente", "Comportamiento", "Estado", "Total", "Cantidad"};

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (Venta v : ventas) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(v.getId() != null ? v.getId() : 0);
                row.createCell(1).setCellValue(v.getFecha() != null ? v.getFecha().format(FMT_DATE) : "");
                row.createCell(2).setCellValue(v.getCliente() != null ? v.getCliente().getNombre() : "");
                row.createCell(3).setCellValue(v.getComportamiento() != null ? v.getComportamiento() : "");
                row.createCell(4).setCellValue(v.getEstado() != null ? v.getEstado() : "");
                row.createCell(5).setCellValue(v.getTotal() != null ? v.getTotal().doubleValue() : 0);
                row.createCell(6).setCellValue(v.getCantidad() != null ? v.getCantidad() : 0);
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(baos);
            log.info("Excel generado con {} ventas", ventas.size());
        } catch (IOException e) {
            log.error("Error al generar Excel", e);
            throw new RuntimeException("Error al generar Excel: " + e.getMessage(), e);
        }
        return baos;
    }

    @Override
    public String obtenerNombreArchivo(Venta venta) {
        if (venta == null) return "venta.pdf";
        String comp = StringUtils.hasText(venta.getComportamiento())
                ? venta.getComportamiento().trim().toUpperCase(Locale.ROOT) : "VENTA";
        String folio = venta.getMovid() != null
                ? venta.getMovid()
                : String.format("%06d", venta.getId() != null ? venta.getId() : 0);
        return comp + "-" + folio + ".pdf";
    }

    @Override
    public void finalizarVenta(Object venta) throws Exception {

    }

    // ─────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────

    private void addInfoCell(PdfPTable table, String label, String value, Font fontLabel, Font fontValue) {
        PdfPCell lCell = new PdfPCell(new Phrase(label, fontLabel));
        lCell.setBackgroundColor(new Color(235, 237, 245));
        lCell.setPadding(4);
        lCell.setBorderColor(new Color(204, 204, 204));
        table.addCell(lCell);

        PdfPCell vCell = new PdfPCell(new Phrase(value != null ? value : "N/A", fontValue));
        vCell.setPadding(4);
        vCell.setBorderColor(new Color(204, 204, 204));
        table.addCell(vCell);
    }

    private void addDetalleRow(PdfPTable table, int idx, String producto, Integer cantidad,
                                BigDecimal precio, BigDecimal subtotal, Font font, Color bg) {
        String[] vals = {
            String.valueOf(idx), producto,
            cantidad != null ? String.valueOf(cantidad) : "0",
            fmt(precio), fmt(subtotal)
        };
        int[] align = {Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_CENTER,
                       Element.ALIGN_RIGHT, Element.ALIGN_RIGHT};
        for (int i = 0; i < vals.length; i++) {
            PdfPCell c = new PdfPCell(new Phrase(vals[i], font));
            c.setBackgroundColor(bg);
            c.setPadding(4);
            c.setHorizontalAlignment(align[i]);
            c.setBorderColor(new Color(220, 220, 220));
            table.addCell(c);
        }
    }

    private void addTotalRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell lCell = new PdfPCell(new Phrase(label, font));
        lCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        lCell.setPadding(4);
        lCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        table.addCell(lCell);

        PdfPCell vCell = new PdfPCell(new Phrase(value, font));
        vCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        vCell.setPadding(4);
        vCell.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
        table.addCell(vCell);
    }

    private String notaFinal(String comp) {
        return switch (comp) {
            case "FACTURA"     -> "Documento fiscal válido para propósitos tributarios.";
            case "PEDIDO"      -> "Orden de compra. Sujeta a confirmación y disponibilidad.";
            case "DEVOLUCION"  -> "Comprobante de devolución. Aplica para cambios o reintegros.";
            case "NOTA_CREDITO"-> "Nota de crédito válida para ajustes contables.";
            default            -> "Documento generado por el sistema MAZER VENTAS.";
        };
    }

    private String fmt(BigDecimal v) {
        return v != null ? String.format("$%,.2f", v) : "$0.00";
    }

    private String nvl(String v) {
        return v != null ? v : "N/A";
    }

    public void finalizarVenta(Long ventaid) throws Exception {
        // Tu lógica para guardar la venta
        System.out.println("Venta guardada exitosamente");

        // Generar factura automáticamente
        byte[] facturaPDF = reporteFacturacionService.procesarFactura(ventaid);

        // Guardar el PDF en el servidor
        Path path = Paths.get("facturas/factura_" + System.currentTimeMillis() + ".pdf");
        Files.createDirectories(path.getParent());
        Files.write(path, facturaPDF);

        System.out.println("Factura generada y guardada en: " + path.toString());
    }

    public void generarCreditoFiscal(Object venta) throws Exception {
        byte[] creditoPDF = reporteFacturacionService.procesarCreditoFiscal(venta);

        Path path = Paths.get("facturas/credito_fiscal_" + System.currentTimeMillis() + ".pdf");
        Files.write(path, creditoPDF);

        System.out.println("Crédito fiscal generado: " + path.toString());
    }

    public void generarNotaCredito(Object notaCredito) throws Exception {
        byte[] notaPDF = reporteFacturacionService.procesarNotaCredito(notaCredito);

        Path path = Paths.get("facturas/nota_credito_" + System.currentTimeMillis() + ".pdf");
        Files.write(path, notaPDF);

        System.out.println("Nota de crédito generada: " + path.toString());
    }
}
