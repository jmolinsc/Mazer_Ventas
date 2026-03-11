package com.deyhayenterprise.mazeradmintemplate.web.form;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VentaCreateForm {

    @NotNull(message = "Debe seleccionar un cliente")
    private Long clienteId;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @Valid
    @Size(min = 1, message = "Debe agregar al menos un producto al detalle")
    private List<VentaDetalleForm> detalles = new ArrayList<>();
}
