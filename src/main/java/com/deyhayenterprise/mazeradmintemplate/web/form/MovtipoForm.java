package com.deyhayenterprise.mazeradmintemplate.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovtipoForm {

    @NotBlank(message = "El nombre del movimiento es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotNull(message = "Debe seleccionar un comportamiento")
    private Long comportamientoId;
}

