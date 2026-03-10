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

import com.deyhayenterprise.mazeradmintemplate.service.ClienteCategoriaService;
import com.deyhayenterprise.mazeradmintemplate.service.ClienteService;
import com.deyhayenterprise.mazeradmintemplate.web.form.ClienteCategoriaForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.ClienteCreateForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Slf4j
public class ClientesController {

    private final ClienteService clienteService;
    private final ClienteCategoriaService clienteCategoriaService;

    @GetMapping("/nuevo")
    public String nuevoCliente(Model model) {
        log.info("Nuevo cliente");
        model.addAttribute("pageHeading", "Nuevo Cliente");
        model.addAttribute("pageSubtitle", "Registrar un nuevo cliente");
        model.addAttribute("categorias", clienteCategoriaService.findAllCategoryNames());
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
            model.addAttribute("categorias", clienteCategoriaService.findAllCategoryNames());
            return "clientes/nuevo";
        }

        try {
            clienteService.create(clienteForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente creado correctamente.");
            return "redirect:/clientes/listar";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Nuevo Cliente");
            model.addAttribute("pageSubtitle", "Registrar un nuevo cliente");
            model.addAttribute("categorias", clienteCategoriaService.findAllCategoryNames());
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
        model.addAttribute("categorias", clienteCategoriaService.findAllActive());
        if (!model.containsAttribute("categoriaForm")) {
            model.addAttribute("categoriaForm", new ClienteCategoriaForm());
        }
        model.addAttribute("editMode", false);
        return "clientes/categorias";
    }

    @PostMapping("/categorias")
    public String crearCategoria(@Valid @ModelAttribute("categoriaForm") ClienteCategoriaForm categoriaForm,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Categorías de Clientes");
            model.addAttribute("pageSubtitle", "Administrar categorías de clientes");
            model.addAttribute("categorias", clienteCategoriaService.findAllActive());
            model.addAttribute("editMode", false);
            return "clientes/categorias";
        }
        try {
            clienteCategoriaService.create(categoriaForm);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría creada correctamente.");
            return "redirect:/clientes/categorias";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Categorías de Clientes");
            model.addAttribute("pageSubtitle", "Administrar categorías de clientes");
            model.addAttribute("categorias", clienteCategoriaService.findAllActive());
            model.addAttribute("editMode", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "clientes/categorias";
        }
    }

    @GetMapping("/categorias/editar/{id}")
    public String editarCategoria(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var categoria = clienteCategoriaService.findActiveById(id);
            ClienteCategoriaForm form = new ClienteCategoriaForm();
            form.setNombre(categoria.getNombre());
            form.setDescripcion(categoria.getDescripcion());

            model.addAttribute("pageHeading", "Categorías de Clientes");
            model.addAttribute("pageSubtitle", "Administrar categorías de clientes");
            model.addAttribute("categorias", clienteCategoriaService.findAllActive());
            model.addAttribute("categoriaForm", form);
            model.addAttribute("editMode", true);
            model.addAttribute("categoriaId", id);
            return "clientes/categorias";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/clientes/categorias";
        }
    }

    @PostMapping("/categorias/editar/{id}")
    public String actualizarCategoria(@PathVariable Long id,
                                      @Valid @ModelAttribute("categoriaForm") ClienteCategoriaForm categoriaForm,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Categorías de Clientes");
            model.addAttribute("pageSubtitle", "Administrar categorías de clientes");
            model.addAttribute("categorias", clienteCategoriaService.findAllActive());
            model.addAttribute("editMode", true);
            model.addAttribute("categoriaId", id);
            return "clientes/categorias";
        }
        try {
            clienteCategoriaService.update(id, categoriaForm);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría actualizada correctamente.");
            return "redirect:/clientes/categorias";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Categorías de Clientes");
            model.addAttribute("pageSubtitle", "Administrar categorías de clientes");
            model.addAttribute("categorias", clienteCategoriaService.findAllActive());
            model.addAttribute("editMode", true);
            model.addAttribute("categoriaId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "clientes/categorias";
        }
    }

    @PostMapping("/categorias/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteCategoriaService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría eliminada correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/clientes/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editarCliente(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("pageHeading", "Editar Cliente");
            model.addAttribute("pageSubtitle", "Actualizar datos del cliente");
            model.addAttribute("categorias", clienteCategoriaService.findAllCategoryNames());
            model.addAttribute("clienteId", id);
            if (!model.containsAttribute("clienteForm")) {
                ClienteCreateForm form = new ClienteCreateForm();
                var cliente = clienteService.findActiveById(id);
                form.setNombre(cliente.getNombre());
                form.setEmail(cliente.getEmail());
                form.setTelefono(cliente.getTelefono());
                form.setCategoria(cliente.getCategoria());
                form.setDireccion(cliente.getDireccion());
                model.addAttribute("clienteForm", form);
            }
            return "clientes/editar";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/clientes/listar";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarCliente(@PathVariable Long id,
                                    @Valid @ModelAttribute("clienteForm") ClienteCreateForm clienteForm,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Editar Cliente");
            model.addAttribute("pageSubtitle", "Actualizar datos del cliente");
            model.addAttribute("categorias", clienteCategoriaService.findAllCategoryNames());
            model.addAttribute("clienteId", id);
            return "clientes/editar";
        }

        try {
            clienteService.update(id, clienteForm);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente actualizado correctamente.");
            return "redirect:/clientes/listar";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Editar Cliente");
            model.addAttribute("pageSubtitle", "Actualizar datos del cliente");
            model.addAttribute("categorias", clienteCategoriaService.findAllCategoryNames());
            model.addAttribute("clienteId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "clientes/editar";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarCliente(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            clienteService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Cliente eliminado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/clientes/listar";
    }
}
