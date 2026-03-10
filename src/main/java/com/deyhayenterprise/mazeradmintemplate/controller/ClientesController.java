package com.deyhayenterprise.mazeradmintemplate.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.deyhayenterprise.mazeradmintemplate.service.ClienteService;
import com.deyhayenterprise.mazeradmintemplate.web.form.ClienteCreateForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClientesController {

    private static final List<String> CATEGORIAS = List.of("VIP", "Regular", "Premium", "Mayorista");

    private final ClienteService clienteService;

    @GetMapping("/nuevo")
    public String nuevoCliente(Model model) {
        log.info("Nuevo cliente");
        model.addAttribute("pageHeading", "Nuevo Cliente");
        model.addAttribute("pageSubtitle", "Registrar un nuevo cliente");
        model.addAttribute("categorias", CATEGORIAS);
        if (!model.containsAttribute("clienteForm")) {
            model.addAttribute("clienteForm", new ClienteCreateForm());
        }
        return "clientes/nuevo";
    }

    @PostMapping("/nuevo")
    public String crearCliente(@Valid @ModelAttribute("clienteForm") ClienteCreateForm clienteForm,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Nuevo Cliente");
            model.addAttribute("pageSubtitle", "Registrar un nuevo cliente");
            model.addAttribute("categorias", CATEGORIAS);
            return "clientes/nuevo";
        }

        try {
            clienteService.create(clienteForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente creado correctamente.");
            return "redirect:/clientes/listar";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Nuevo Cliente");
            model.addAttribute("pageSubtitle", "Registrar un nuevo cliente");
            model.addAttribute("categorias", CATEGORIAS);
            model.addAttribute("errorMessage", ex.getMessage());
            return "clientes/nuevo";
        }
    }

    @GetMapping("/listar")
    public String listarClientes(Model model) {
        log.info("Listar clientes");
        model.addAttribute("pageHeading", "Listar Clientes");
        model.addAttribute("pageSubtitle", "Base de datos de clientes");
        model.addAttribute("clientes", clienteService.findAll());
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

