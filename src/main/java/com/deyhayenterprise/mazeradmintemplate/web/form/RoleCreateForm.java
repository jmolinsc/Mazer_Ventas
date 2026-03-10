package com.deyhayenterprise.mazeradmintemplate.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleCreateForm {

    @NotBlank(message = "El código del rol es obligatorio")
    @Size(min = 3, max = 50, message = "El código debe tener entre 3 y 50 caracteres")
    @Pattern(regexp = "^[a-zA-Z0-9_\\- ]+$", message = "El código solo permite letras, números, espacios, guion y guion bajo")
    private String codigo;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;
}

