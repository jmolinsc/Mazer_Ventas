package com.deyhayenterprise.mazeradmintemplate.web.form;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VentaCreateForm {

    @NotNull(message = "Debe seleccionar un cliente")
    private Long clienteId;

    @NotNull(message = "Debe seleccionar un producto")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;
}

