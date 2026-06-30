package com.deyhayenterprise.mazeradmintemplate.service.reporte;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

@Service
public class JasperReportGenericService {

    /**
     * Método genérico y centralizado para generar PDFs a partir de colecciones de objetos Java.
     */
    public byte[] generarReportePdf(String nombreArchivo, Map<String, Object> parametros, Collection<?> datos) {
        try {
            // CORRECCIÓN: Cambiado de "/reportes/" a "/reports/" para coincidir con tu carpeta real
            String rutaCompleta = "/reports/" + nombreArchivo + ".jrxml";
            InputStream stream = getClass().getResourceAsStream(rutaCompleta);

            if (stream == null) {
                throw new IllegalArgumentException("No se encontró el archivo del reporte en: " + rutaCompleta);
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(stream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(datos);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

            return JasperExportManager.exportReportToPdf(jasperPrint);

        } catch (JRException e) {
            throw new RuntimeException("Error crítico al procesar el reporte " + nombreArchivo + ": " + e.getMessage(), e);
        }
    }
}
