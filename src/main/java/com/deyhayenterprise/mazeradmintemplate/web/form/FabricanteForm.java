package com.deyhayenterprise.mazeradmintemplate.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FabricanteForm {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 140, message = "El nombre no puede superar 140 caracteres")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingrese un correo valido")
    private String email;

    @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
    private String telefono;

    @NotBlank(message = "El pais es obligatorio")
    @Size(max = 80, message = "El pais no puede superar 80 caracteres")
    private String pais;

    @Size(max = 255, message = "La direccion no puede superar 255 caracteres")
    private String direccion;
}

