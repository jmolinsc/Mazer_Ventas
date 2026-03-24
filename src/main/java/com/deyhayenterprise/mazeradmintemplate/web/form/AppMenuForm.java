package com.deyhayenterprise.mazeradmintemplate.web.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppMenuForm {

    @NotBlank(message = "El código del menú es obligatorio")
    @Size(max = 60, message = "El código no puede superar 60 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del menú es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    private String nombre;

    @NotBlank(message = "La sección es obligatoria")
    @Size(max = 120, message = "La sección no puede superar 120 caracteres")
    private String seccion;

    @Size(max = 80, message = "El icono no puede superar 80 caracteres")
    private String icono;

    @NotNull(message = "El orden visual es obligatorio")
    @Min(value = 0, message = "El orden visual no puede ser negativo")
    private Integer ordenVisual;
}

