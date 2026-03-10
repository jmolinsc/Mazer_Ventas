package com.deyhayenterprise.mazeradmintemplate.web.form;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateForm {

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 4, max = 60, message = "El usuario debe tener entre 4 y 60 caracteres")
    private String username;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 140, message = "El nombre completo no puede superar 140 caracteres")
    private String nombreCompleto;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingrese un correo válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 120, message = "La contraseña debe tener entre 8 y 120 caracteres")
    private String password;

    @NotEmpty(message = "Debe seleccionar al menos un rol")
    private Set<Long> roleIds = new LinkedHashSet<>();
}

