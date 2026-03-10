package com.deyhayenterprise.mazeradmintemplate.web.form;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoCreateForm {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 140, message = "El nombre no puede superar 140 caracteres")
    private String nombre;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50, message = "El código no puede superar 50 caracteres")
    private String codigo;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 60, message = "La categoría no puede superar 60 caracteres")
    private String categoria;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotBlank(message = "La unidad es obligatoria")
    @Size(max = 20, message = "La unidad no puede superar 20 caracteres")
    private String unidad;
}

