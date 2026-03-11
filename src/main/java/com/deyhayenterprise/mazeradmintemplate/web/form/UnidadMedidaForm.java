package com.deyhayenterprise.mazeradmintemplate.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnidadMedidaForm {

    @NotBlank(message = "El nombre de la unidad es obligatorio")
    @Size(max = 40, message = "El nombre no puede superar 40 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripcion no puede superar 255 caracteres")
    private String descripcion;
}

