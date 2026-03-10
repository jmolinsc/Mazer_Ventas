package com.deyhayenterprise.mazeradmintemplate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClientesController {

    @GetMapping("/nuevo")
    public String nuevoCliente(Model model) {
        log.info("Nuevo cliente");
        model.addAttribute("pageHeading", "Nuevo Cliente");
        model.addAttribute("pageSubtitle", "Registrar un nuevo cliente");
        return "clientes/nuevo";
    }

    @GetMapping("/listar")
    public String listarClientes(Model model) {
        log.info("Listar clientes");
        model.addAttribute("pageHeading", "Listar Clientes");
        model.addAttribute("pageSubtitle", "Base de datos de clientes");
        return "clientes/listar";
    }

    @GetMapping("/categorias")
    public String categoriasClientes(Model model) {
        log.info("Categorías de clientes");
        model.addAttribute("pageHeading", "Categorías de Clientes");
        model.addAttribute("pageSubtitle", "Administrar categorías de clientes");
        return "clientes/categorias";
    }
}

