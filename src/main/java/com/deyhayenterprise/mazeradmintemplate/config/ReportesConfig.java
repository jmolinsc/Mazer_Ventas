package com.deyhayenterprise.mazeradmintemplate.config;


import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ReportesConfig {

    @PostConstruct
    public void initReportesDirectory() {
        try {
            // Crear directorio de reportes si no existe
            Path reportPath = Paths.get("facturas");
            if (!Files.exists(reportPath)) {
                Files.createDirectories(reportPath);
                System.out.println("Directorio de facturas creado: " + reportPath.toAbsolutePath());
            }

            // Verificar que existan los archivos .jrxml
            String[] reportes = {"factura", "credito_fiscal", "nota_credito", "nota_debito"};
            for (String reporte : reportes) {
                String path = "reports/" + reporte + ".jrxml";
                ClassPathResource resource = new ClassPathResource(path);
                if (!resource.exists()) {
                    System.out.println("ADVERTENCIA: No se encontró el reporte: " + path);
                } else {
                    System.out.println("Reporte encontrado: " + path);
                }
            }
        } catch (Exception e) {
            System.err.println("Error inicializando directorio de reportes: " + e.getMessage());
        }
    }
}