package com.deyhayenterprise.mazeradmintemplate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO que representa los datos de la empresa
 * Este objeto será un Bean global en Spring
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaGlobal implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private String razonsocial;
    private String nit;
    private String nrc;
    private String email;
    private String telefono;
    private String direccion;


    // Métodos helper para acceso rápido
    public String getNombreCompleto() {
        return nombre != null ? nombre : "Empresa";
    }

    public String getDireccionCompleta() {
        return direccion;
    }
}