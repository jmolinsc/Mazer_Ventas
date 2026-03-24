package com.deyhayenterprise.mazeradmintemplate.web.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuOptionForm {

    @NotBlank(message = "El código de la opción es obligatorio")
    @Size(max = 80, message = "El código no puede superar 80 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre de la opción es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    private String nombre;

    @NotBlank(message = "La URL es obligatoria")
    @Size(max = 180, message = "La URL no puede superar 180 caracteres")
    private String url;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;

    @NotNull(message = "El orden visual es obligatorio")
    @Min(value = 0, message = "El orden visual no puede ser negativo")
    private Integer ordenVisual;

    @NotNull(message = "Debe seleccionar un menú")
    private Long menuId;
}

