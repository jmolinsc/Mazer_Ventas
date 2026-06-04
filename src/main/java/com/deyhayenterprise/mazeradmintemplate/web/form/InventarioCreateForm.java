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
public class InventarioCreateForm {

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fecha;

    @Size(max = 100, message = "El movimiento no puede exceder 100 caracteres")
    private String mov;

    @Size(max = 50, message = "El movid no puede exceder 50 caracteres")
    private String movid;

    @Size(max = 100, message = "El comportamiento no puede exceder 100 caracteres")
    private String comportamiento;

    @Valid
    @Size(min = 1, message = "Debe agregar al menos un producto al detalle")
    private List<InventarioDetalleForm> detalles = new ArrayList<>();
}

