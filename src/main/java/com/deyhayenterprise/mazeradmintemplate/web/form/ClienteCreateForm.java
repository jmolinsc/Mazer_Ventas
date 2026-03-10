package com.deyhayenterprise.mazeradmintemplate.web.form;

import jakarta.validation.constraints.Email;
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
}

