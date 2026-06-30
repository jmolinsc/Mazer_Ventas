package com.deyhayenterprise.mazeradmintemplate.service.reporte;

import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportesNegocioService {

    @Autowired
    private JasperReportGenericService genericService;

    // Supongamos que tienes repositorios inyectados aquí para obtener la información
    // @Autowired private VentaRepository ventaRepository;

    public byte[] generarReporteVentas(List<Venta> listaVentas, String nombreUsuario) {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("TITULO_REPORTE", "Resumen Ejecutivo de Ventas");
        parametros.put("CREADO_POR", nombreUsuario);

        // Invoca al método genérico pasando el nombre exacto del archivo .jrxml sin la extensión
        return genericService.generarReportePdf("factura", parametros, listaVentas);
    }

    public byte[] generarReporteCompras(List<Venta> listaCompras) {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("TITULO_REPORTE", "Reporte de Órdenes de Compra");

        return genericService.generarReportePdf("reporte_compras", parametros, listaCompras);
    }

    public byte[] generarReporteInventario(List<Venta> listaInventario) {
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("TITULO_REPORTE", "Stock y Auditoría de Inventario");

        return genericService.generarReportePdf("reporte_inventario", parametros, listaInventario);
    }
}
