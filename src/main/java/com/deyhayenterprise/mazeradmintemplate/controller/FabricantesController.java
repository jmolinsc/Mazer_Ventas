package com.deyhayenterprise.mazeradmintemplate.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.deyhayenterprise.mazeradmintemplate.service.FabricanteService;
import com.deyhayenterprise.mazeradmintemplate.web.form.FabricanteForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/fabricantes")
@RequiredArgsConstructor
@Slf4j
public class FabricantesController {

    private static final List<String> PAISES = List.of(
            "Colombia", "Mexico", "Argentina", "Peru", "Chile",
            "Brasil", "Espana", "USA", "China", "Japon", "Alemania");

    private final FabricanteService fabricanteService;

    // ─── NUEVO ──────────────────────────────────────────────────────────────────

    @GetMapping("/nuevo")
    public String nuevoFabricante(Model model) {
        log.info("Nuevo fabricante");
        model.addAttribute("pageHeading", "Nuevo Fabricante");
        model.addAttribute("pageSubtitle", "Registrar un nuevo fabricante");
        model.addAttribute("paises", PAISES);
        if (!model.containsAttribute("fabricanteForm")) {
            model.addAttribute("fabricanteForm", new FabricanteForm());
        }
        return "fabricantes/nuevo";
    }

    @PostMapping("/nuevo")
    public String crearFabricante(@Valid @ModelAttribute("fabricanteForm") FabricanteForm fabricanteForm,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Nuevo Fabricante");
            model.addAttribute("pageSubtitle", "Registrar un nuevo fabricante");
            model.addAttribute("paises", PAISES);
            return "fabricantes/nuevo";
        }
        try {
            fabricanteService.create(fabricanteForm);
            redirectAttributes.addFlashAttribute("successMessage", "Fabricante creado correctamente.");
            return "redirect:/fabricantes/listar";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Nuevo Fabricante");
            model.addAttribute("pageSubtitle", "Registrar un nuevo fabricante");
            model.addAttribute("paises", PAISES);
            model.addAttribute("errorMessage", ex.getMessage());
            return "fabricantes/nuevo";
        }
    }

    // ─── LISTAR ─────────────────────────────────────────────────────────────────

    @GetMapping("/listar")
    public String listarFabricantes(Model model) {
        log.info("Listar fabricantes");
        model.addAttribute("pageHeading", "Listar Fabricantes");
        model.addAttribute("pageSubtitle", "Base de datos de fabricantes");
        model.addAttribute("fabricantes", fabricanteService.findAll());
        return "fabricantes/listar";
    }

    // ─── EDITAR ─────────────────────────────────────────────────────────────────

    @GetMapping("/editar/{id}")
    public String editarFabricante(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("pageHeading", "Editar Fabricante");
            model.addAttribute("pageSubtitle", "Actualizar datos del fabricante");
            model.addAttribute("paises", PAISES);
            model.addAttribute("fabricanteId", id);
            if (!model.containsAttribute("fabricanteForm")) {
                var f = fabricanteService.findActiveById(id);
                FabricanteForm form = new FabricanteForm();
                form.setNombre(f.getNombre());
                form.setEmail(f.getEmail());
                form.setTelefono(f.getTelefono());
                form.setPais(f.getPais());
                form.setDireccion(f.getDireccion());
                model.addAttribute("fabricanteForm", form);
            }
            return "fabricantes/editar";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/fabricantes/listar";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarFabricante(@PathVariable Long id,
                                       @Valid @ModelAttribute("fabricanteForm") FabricanteForm fabricanteForm,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Editar Fabricante");
            model.addAttribute("pageSubtitle", "Actualizar datos del fabricante");
            model.addAttribute("paises", PAISES);
            model.addAttribute("fabricanteId", id);
            return "fabricantes/editar";
        }
        try {
            fabricanteService.update(id, fabricanteForm);
            redirectAttributes.addFlashAttribute("successMessage", "Fabricante actualizado correctamente.");
            return "redirect:/fabricantes/listar";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Editar Fabricante");
            model.addAttribute("pageSubtitle", "Actualizar datos del fabricante");
            model.addAttribute("paises", PAISES);
            model.addAttribute("fabricanteId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "fabricantes/editar";
        }
    }

    // ─── ELIMINAR ────────────────────────────────────────────────────────────────

    @PostMapping("/eliminar/{id}")
    public String eliminarFabricante(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            fabricanteService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Fabricante eliminado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/fabricantes/listar";
    }
}
