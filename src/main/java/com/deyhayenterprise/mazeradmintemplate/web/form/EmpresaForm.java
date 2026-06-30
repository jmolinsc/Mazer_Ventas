package com.deyhayenterprise.mazeradmintemplate.web.form;

import com.deyhayenterprise.mazeradmintemplate.entity.Empresa;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaForm {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 140, message = "El nombre no puede superar 140 caracteres")
    private String nombre;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 140, message = "El nombre no puede superar 140 caracteres")
    private String razonsocial;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingrese un correo valido")
    private String email;

    @NotBlank(message = "El NIT es obligatorio")
    private String nit;

    @NotBlank(message = "El NRC es obligatorio")
    private String nrc;

    @Size(max = 30, message = "El telefono no puede superar 30 caracteres")
    private String telefono;

    @Size(max = 255, message = "La direccion no puede superar 255 caracteres")
    private String direccion;



    public EmpresaForm() {
        // Constructor vacío para frameworks de validación
    }

    // Constructor específico para Empresa
    public EmpresaForm(Empresa empresa) {
        this.nombre = empresa.getNombre();
        this.razonsocial = empresa.getRazonsocial();
        this.email = empresa.getEmail();
        this.telefono = empresa.getTelefono();
        this.direccion = empresa.getDireccion();
        this.nrc=empresa.getNrc();
        this.nit=empresa.getNit();
    }

}
