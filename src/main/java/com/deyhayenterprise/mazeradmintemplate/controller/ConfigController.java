package com.deyhayenterprise.mazeradmintemplate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/config")
@RequiredArgsConstructor
@Slf4j
public class ConfigController {

    @GetMapping("/empresa")
    public String empresa(Model model) {
        log.info("Configuración de empresa");
        model.addAttribute("pageHeading", "Datos de la Empresa");
        model.addAttribute("pageSubtitle", "Información general de la empresa");
        return "config/empresa";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        log.info("Gestión de usuarios");
        model.addAttribute("pageHeading", "Gestión de Usuarios");
        model.addAttribute("pageSubtitle", "Administrar usuarios del sistema");
        return "config/usuarios";
    }

    @GetMapping("/permisos")
    public String permisos(Model model) {
        log.info("Gestión de permisos");
        model.addAttribute("pageHeading", "Gestión de Permisos");
        model.addAttribute("pageSubtitle", "Configurar permisos de usuarios");
        return "config/permisos";
    }
}

