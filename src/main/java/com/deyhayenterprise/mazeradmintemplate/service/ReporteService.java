package com.deyhayenterprise.mazeradmintemplate.service;

import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import java.io.ByteArrayOutputStream;

/**
 * Servicio para generar reportes de ventas en PDF y otros formatos.
 * Soporta diferentes comportamientos: FACTURA, PEDIDO, DEVOLUCION, NOTA_CREDITO
 */
public interface ReporteService {

    /**
     * Genera un PDF del comprobante de venta según su comportamiento
     * @param venta Entidad Venta con detalles
     * @return ByteArrayOutputStream con el contenido PDF
     */
    ByteArrayOutputStream generarPDF(Venta venta);

    /**
     * Genera un PDF personalizado según el tipo de comportamiento
     * @param venta Entidad Venta
     * @param comportamiento Tipo de documento (FACTURA, PEDIDO, etc.)
     * @return ByteArrayOutputStream con PDF
     */
    ByteArrayOutputStream generarPDFPorComportamiento(Venta venta, String comportamiento);

    /**
     * Exporta lista de ventas a Excel
     * @param ventas Lista de ventas a exportar
     * @return ByteArrayOutputStream con contenido Excel
     */
    ByteArrayOutputStream exportarVentasExcel(java.util.List<Venta> ventas);

    /**
     * Obtiene el nombre del archivo según el tipo de documento
     * @param venta Entidad Venta
     * @return Nombre sugerido del archivo (ej: "FACTURA-VTA-000001.pdf")
     */
    String obtenerNombreArchivo(Venta venta);


    public void finalizarVenta(Object venta)throws Exception;
    public void generarCreditoFiscal(Object venta)throws Exception;
    public void generarNotaCredito(Object notaCredito)throws Exception;
}

