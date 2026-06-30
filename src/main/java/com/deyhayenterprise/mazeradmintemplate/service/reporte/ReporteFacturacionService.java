package com.deyhayenterprise.mazeradmintemplate.service.reporte;


import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Service
public class ReporteFacturacionService {



    @Autowired
    private DataSource dataSource; // Spring Boot inyecta automáticamente la conexión a tu BD


    private static final String REPORT_PATH = "reports/";

    private final Map<String, JasperReport> reportCache = new HashMap<>();

    public byte[] procesarFactura(Long id) {
        try {
            Map<String, Object> params = new HashMap<>();
            // El nombre "ventaId" debe coincidir EXACTAMENTE con el <parameter name="ventaId"...> de tu JRXML
            params.put("ventaId", id);
            return generarReporte("factura3", params);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la factura: " + e.getMessage(), e);
        }
    }


    public byte[] procesarCreditoFiscal(Object venta) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TITULO", "CRÉDITO FISCAL");
            params.put("FECHA_EMISION", new Date());

            List<Object> listaVentas = Arrays.asList(venta);
            return generarReporte("credito_fiscal", params);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el crédito fiscal: " + e.getMessage(), e);
        }
    }

    public byte[] procesarNotaCredito(Object notaCredito) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TITULO", "NOTA DE CRÉDITO");
            params.put("FECHA_EMISION", new Date());

            List<Object> listaNotas = Arrays.asList(notaCredito);
            return generarReporte("nota_credito", params);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la nota de crédito: " + e.getMessage(), e);
        }
    }

    public byte[] procesarNotaDebito(Object notaDebito) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("TITULO", "NOTA DE DÉBITO");
            params.put("FECHA_EMISION", new Date());

            List<Object> listaNotas = Arrays.asList(notaDebito);
            return generarReporte("nota_debito", params);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la nota de débito: " + e.getMessage(), e);
        }
    }

    /**
     * Método modificado para reportes con consultas SQL nativas internas
     */
    public byte[] generarReporte(String nombreReporte, Map<String, Object> parametros) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Abrimos un bloque try-with-resources para asegurar que la conexión de la BD se cierre siempre
        try (Connection connection = dataSource.getConnection()) {

            JasperReport jasperReport = getCompiledReport(nombreReporte);

            // REEMPLAZO CLAVE: Pasamos 'connection' en lugar de 'dataSource' (JRBeanCollectionDataSource)
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, connection);

            exportToPdf(jasperPrint, outputStream);
            return outputStream.toByteArray();

        } catch (JRException e) {
            throw new RuntimeException("Error al generar reporte " + nombreReporte + ": " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeException("Error de conexión a la base de datos al generar " + nombreReporte + ": " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] generarReporteSQL(String nombreReporte, Map<String, Object> parametros) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            JasperReport jasperReport = getCompiledReport(nombreReporte);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource.getConnection());
            exportToPdf(jasperPrint, outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar reporte SQL " + nombreReporte + ": " + e.getMessage(), e);
        }
    }

    private JasperReport getCompiledReport(String nombreReporte) throws JRException, IOException {
        if (reportCache.containsKey(nombreReporte)) {
            return reportCache.get(nombreReporte);
        }

        String jrxmlPath = REPORT_PATH + nombreReporte + ".jrxml";
        ClassPathResource resource = new ClassPathResource(jrxmlPath);

        if (!resource.exists()) {
            throw new FileNotFoundException("Reporte no encontrado: " + jrxmlPath);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
            reportCache.put(nombreReporte, jasperReport);
            return jasperReport;
        }
    }

    private void exportToPdf(JasperPrint jasperPrint, OutputStream outputStream) throws JRException {
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
        configuration.setCreatingBatchModeBookmarks(true);
        exporter.setConfiguration(configuration);

        exporter.exportReport();
    }
}
