package com.deyhayenterprise.mazeradmintemplate.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ComportamientoForm {

    @NotBlank(message = "El nombre del comportamiento es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
    private String nombre;

    @NotNull(message = "Debe seleccionar un módulo")
    private Long moduloId;
}

