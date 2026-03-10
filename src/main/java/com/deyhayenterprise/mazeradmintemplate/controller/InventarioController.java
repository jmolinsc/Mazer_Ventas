package com.deyhayenterprise.mazeradmintemplate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/inventario")
@RequiredArgsConstructor
@Slf4j
public class InventarioController {

    @GetMapping("/movimientos")
    public String movimientos(Model model) {
        log.info("Movimientos de inventario");
        model.addAttribute("pageHeading", "Movimientos de Inventario");
        model.addAttribute("pageSubtitle", "Registro de entradas y salidas");
        return "inventario/movimientos";
    }

    @GetMapping("/existencias")
    public String existencias(Model model) {
        log.info("Existencias en inventario");
        model.addAttribute("pageHeading", "Existencias");
        model.addAttribute("pageSubtitle", "Stock disponible por producto");
        return "inventario/existencias";
    }

    @GetMapping("/ajustes")
    public String ajustes(Model model) {
        log.info("Ajustes de inventario");
        model.addAttribute("pageHeading", "Ajustes de Inventario");
        model.addAttribute("pageSubtitle", "Correcciones de stock");
        return "inventario/ajustes";
    }
}

