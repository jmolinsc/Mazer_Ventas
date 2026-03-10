package com.deyhayenterprise.mazeradmintemplate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/fabricantes")
@RequiredArgsConstructor
@Slf4j
public class FabricantesController {

    @GetMapping("/nuevo")
    public String nuevoFabricante(Model model) {
        log.info("Nuevo fabricante");
        model.addAttribute("pageHeading", "Nuevo Fabricante");
        model.addAttribute("pageSubtitle", "Registrar un nuevo fabricante");
        return "fabricantes/nuevo";
    }

    @GetMapping("/listar")
    public String listarFabricantes(Model model) {
        log.info("Listar fabricantes");
        model.addAttribute("pageHeading", "Listar Fabricantes");
        model.addAttribute("pageSubtitle", "Base de datos de fabricantes");
        return "fabricantes/listar";
    }
}

