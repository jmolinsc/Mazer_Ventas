package com.deyhayenterprise.mazeradmintemplate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
@Slf4j
public class ProductosController {

    @GetMapping("/nuevo")
    public String nuevoProducto(Model model) {
        log.info("Nuevo producto");
        model.addAttribute("pageHeading", "Nuevo Producto");
        model.addAttribute("pageSubtitle", "Registrar un nuevo producto");
        return "productos/nuevo";
    }

    @GetMapping("/listar")
    public String listarProductos(Model model) {
        log.info("Listar productos");
        model.addAttribute("pageHeading", "Listar Productos");
        model.addAttribute("pageSubtitle", "Catálogo de productos");
        return "productos/listar";
    }

    @GetMapping("/categorias")
    public String categoriasProductos(Model model) {
        log.info("Categorías de productos");
        model.addAttribute("pageHeading", "Categorías de Productos");
        model.addAttribute("pageSubtitle", "Administrar categorías");
        return "productos/categorias";
    }
}

