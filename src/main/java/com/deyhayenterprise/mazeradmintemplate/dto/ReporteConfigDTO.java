package com.deyhayenterprise.mazeradmintemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para facilitar la creación de reportes con opciones personalizadas.
 * Permite pasar configuración flexible al servicio de reportes.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReporteConfigDTO {
    
    /** Tipo de documento: FACTURA, PEDIDO, DEVOLUCION, NOTA_CREDITO */
    private String comportamiento;
    
    /** Incluir marca de agua (para borradores) */
    private Boolean incluirMarcaAgua;
    
    /** Formato de salida: PDF, EXCEL, CSV */
    private String formato;
    
    /** Nota personalizada al pie del reporte */
    private String notaPersonalizada;
    
    /** Incluir datos impositivos (IVA, retención, etc.) */
    private Boolean incluirImpuestos;
    
    /** Número de copia (original, copia 1, copia 2, etc.) */
    private Integer numeroCopia;
    
    /** Enviar por email al cliente */
    private Boolean enviarPorEmail;
    
    /** Email destino (si enviarPorEmail es true) */
    private String emailDestino;
}

