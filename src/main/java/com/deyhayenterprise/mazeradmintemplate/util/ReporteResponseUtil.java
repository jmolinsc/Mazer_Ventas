package com.deyhayenterprise.mazeradmintemplate.util;

import com.deyhayenterprise.mazeradmintemplate.dto.ReporteConfigDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Utilidad para construir respuestas HTTP de reportes con headers apropiados.
 */
@Slf4j
public class ReporteResponseUtil {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Construye una ResponseEntity para descargar un PDF.
     * 
     * @param pdf ByteArrayOutputStream con el contenido PDF
     * @param nombreArchivo Nombre del archivo (ej: "FACTURA-000001.pdf")
     * @return ResponseEntity con headers apropiados
     */
    public static ResponseEntity<byte[]> buildPdfResponse(ByteArrayOutputStream pdf, String nombreArchivo) {
        return buildDownloadResponse(pdf, nombreArchivo, MediaType.APPLICATION_PDF);
    }

    /**
     * Construye una ResponseEntity para descargar un Excel.
     * 
     * @param excel ByteArrayOutputStream con el contenido XLSX
     * @param nombreArchivo Nombre del archivo (ej: "Ventas-2024-06-15.xlsx")
     * @return ResponseEntity con headers apropiados
     */
    public static ResponseEntity<byte[]> buildExcelResponse(ByteArrayOutputStream excel, String nombreArchivo) {
        MediaType mediaType = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        return buildDownloadResponse(excel, nombreArchivo, mediaType);
    }

    /**
     * Construye una ResponseEntity para descargar un CSV.
     * 
     * @param csv ByteArrayOutputStream con el contenido CSV
     * @param nombreArchivo Nombre del archivo (ej: "Ventas-2024-06-15.csv")
     * @return ResponseEntity con headers apropiados
     */
    public static ResponseEntity<byte[]> buildCsvResponse(ByteArrayOutputStream csv, String nombreArchivo) {
        return buildDownloadResponse(csv, nombreArchivo, MediaType.TEXT_PLAIN);
    }

    /**
     * Construye una ResponseEntity genérica para descargar un archivo.
     * 
     * @param contenido ByteArrayOutputStream con el contenido
     * @param nombreArchivo Nombre del archivo
     * @param mediaType Tipo MIME del archivo
     * @return ResponseEntity con headers apropiados
     */
    private static ResponseEntity<byte[]> buildDownloadResponse(
            ByteArrayOutputStream contenido, 
            String nombreArchivo, 
            MediaType mediaType) {
        
        // Sanitizar nombre de archivo
        String nombreSanitizado = sanitizarNombre(nombreArchivo);
        
        // Codificar para ASCII-safe (RFC 5987)
        String encodedFilename;
        try {
            encodedFilename = URLEncoder.encode(nombreSanitizado, StandardCharsets.UTF_8.toString())
                    .replace("+", "%20");
        } catch (Exception e) {
            log.warn("Error al codificar nombre de archivo, usando default", e);
            encodedFilename = "documento";
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + nombreSanitizado + "\"; " +
                        "filename*=UTF-8''" + encodedFilename)
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                .header(HttpHeaders.PRAGMA, "no-cache")
                .header(HttpHeaders.EXPIRES, "0")
                .contentLength(contenido.size())
                .body(contenido.toByteArray());
    }

    /**
     * Genera un nombre de archivo para un lote de reportes.
     * 
     * @param prefijo Prefijo (ej: "VENTAS", "FACTURAS")
     * @return Nombre con fecha (ej: "VENTAS-2024-06-15")
     */
    public static String generarNombreArchivoPorFecha(String prefijo) {
        return prefijo + "-" + LocalDate.now().format(DATE_FMT);
    }

    /**
     * Valida y sanitiza un nombre de archivo.
     * Remueve caracteres peligrosos.
     * 
     * @param nombre Nombre original
     * @return Nombre sanitizado
     */
    public static String sanitizarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "reporte";
        }
        // Remover path traversal y caracteres peligrosos
        return nombre
                .replaceAll("[/\\\\:*?\"<>|]", "_")
                .replaceAll("\\s+", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "")
                .substring(0, Math.min(255, nombre.length()));
    }

    /**
     * Construye una respuesta inline para ver el reporte en el navegador.
     * 
     * @param pdf ByteArrayOutputStream con el PDF
     * @param nombreArchivo Nombre para referencia
     * @return ResponseEntity para visualización inline
     */
    public static ResponseEntity<byte[]> buildPdfInlineResponse(ByteArrayOutputStream pdf, String nombreArchivo) {
        String nombreSanitizado = sanitizarNombre(nombreArchivo);
        
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreSanitizado + "\"")
                .body(pdf.toByteArray());
    }
}

