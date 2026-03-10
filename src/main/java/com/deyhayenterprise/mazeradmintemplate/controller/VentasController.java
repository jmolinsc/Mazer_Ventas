package com.deyhayenterprise.mazeradmintemplate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/ventas")
@RequiredArgsConstructor
@Slf4j
public class VentasController {

    @GetMapping("/nueva")
    public String nuevaVenta(Model model) {
        log.info("Nueva venta");
        model.addAttribute("pageHeading", "Nueva Venta");
        model.addAttribute("pageSubtitle", "Registrar una nueva venta");
        return "ventas/nueva";
    }

    @GetMapping("/listar")
    public String listarVentas(Model model) {
        log.info("Listar ventas");
        model.addAttribute("pageHeading", "Listar Ventas");
        model.addAttribute("pageSubtitle", "Historial de ventas");
        return "ventas/listar";
    }

    @GetMapping("/reportes")
    public String reportesVentas(Model model) {
        log.info("Reportes de ventas");
        model.addAttribute("pageHeading", "Reportes de Ventas");
        model.addAttribute("pageSubtitle", "Análisis de ventas");
        return "ventas/reportes";
    }
}

