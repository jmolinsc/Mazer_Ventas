package com.deyhayenterprise.mazeradmintemplate.web.form;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteCreateForm {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 140, message = "El nombre no puede superar 140 caracteres")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingrese un correo válido")
    private String email;

    @Size(max = 30, message = "El teléfono no puede superar 30 caracteres")
    private String telefono;

    @NotBlank(message = "La categoría es obligatoria")
    private String categoria;

    @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
    private String direccion;

    @DecimalMin(value = "0.00", message = "El limite de credito no puede ser negativo")
    private BigDecimal limiteCredito;

    @Size(max = 120, message = "El agente no puede superar 120 caracteres")
    private String agente;

    @Min(value = 0, message = "Los dias de credito no pueden ser negativos")
    private Integer diasCredito;

    @Size(max = 25, message = "El NIT no puede superar 25 caracteres")
    private String nit;

    @Size(max = 25, message = "El NRC no puede superar 25 caracteres")
    private String nrc;

    @NotBlank(message = "La actividad economica es obligatoria")
    @Size(max = 255, message = "La actividad economica no puede superar 255 caracteres")
    private String actividadEconomica;

    @NotBlank(message = "El pais es obligatorio")
    @Size(max = 80, message = "El pais no puede superar 80 caracteres")
    private String pais;

    @NotBlank(message = "El departamento es obligatorio")
    @Size(max = 80, message = "El departamento no puede superar 80 caracteres")
    private String departamento;

    @NotBlank(message = "El municipio es obligatorio")
    @Size(max = 100, message = "El municipio no puede superar 100 caracteres")
    private String municipio;
}

