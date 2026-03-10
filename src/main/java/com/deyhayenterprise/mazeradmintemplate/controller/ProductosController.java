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

import com.deyhayenterprise.mazeradmintemplate.service.ProductoService;
import com.deyhayenterprise.mazeradmintemplate.web.form.ProductoCreateForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
@Slf4j
public class ProductosController {

    private static final List<String> CATEGORIAS = List.of("Electronica", "Hogar", "Oficina", "Alimentos");

    private final ProductoService productoService;

    @GetMapping("/nuevo")
    public String nuevoProducto(Model model) {
        log.info("Nuevo producto");
        model.addAttribute("pageHeading", "Nuevo Producto");
        model.addAttribute("pageSubtitle", "Registrar un nuevo producto");
        model.addAttribute("categorias", CATEGORIAS);
        if (!model.containsAttribute("productoForm")) {
            model.addAttribute("productoForm", new ProductoCreateForm());
        }
        return "productos/nuevo";
    }

    @PostMapping("/nuevo")
    public String crearProducto(@Valid @ModelAttribute("productoForm") ProductoCreateForm productoForm,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Nuevo Producto");
            model.addAttribute("pageSubtitle", "Registrar un nuevo producto");
            model.addAttribute("categorias", CATEGORIAS);
            return "productos/nuevo";
        }

        try {
            productoService.create(productoForm);
            redirectAttributes.addFlashAttribute("successMessage", "Producto creado correctamente.");
            return "redirect:/productos/listar";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Nuevo Producto");
            model.addAttribute("pageSubtitle", "Registrar un nuevo producto");
            model.addAttribute("categorias", CATEGORIAS);
            model.addAttribute("errorMessage", ex.getMessage());
            return "productos/nuevo";
        }
    }

    @GetMapping("/listar")
    public String listarProductos(Model model) {
        log.info("Listar productos");
        model.addAttribute("pageHeading", "Listar Productos");
        model.addAttribute("pageSubtitle", "Catálogo de productos");
        model.addAttribute("productos", productoService.findAll());
        return "productos/listar";
    }

    @GetMapping("/categorias")
    public String categoriasProductos(Model model) {
        log.info("Categorías de productos");
        model.addAttribute("pageHeading", "Categorías de Productos");
        model.addAttribute("pageSubtitle", "Administrar categorías");
        return "productos/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("pageHeading", "Editar Producto");
            model.addAttribute("pageSubtitle", "Actualizar datos del producto");
            model.addAttribute("categorias", CATEGORIAS);
            model.addAttribute("productoId", id);
            if (!model.containsAttribute("productoForm")) {
                ProductoCreateForm form = new ProductoCreateForm();
                var producto = productoService.findActiveById(id);
                form.setNombre(producto.getNombre());
                form.setCodigo(producto.getCodigo());
                form.setCategoria(producto.getCategoria());
                form.setPrecio(producto.getPrecio());
                form.setStock(producto.getStock());
                form.setUnidad(producto.getUnidad());
                model.addAttribute("productoForm", form);
            }
            return "productos/editar";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/productos/listar";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarProducto(@PathVariable Long id,
                                     @Valid @ModelAttribute("productoForm") ProductoCreateForm productoForm,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Editar Producto");
            model.addAttribute("pageSubtitle", "Actualizar datos del producto");
            model.addAttribute("categorias", CATEGORIAS);
            model.addAttribute("productoId", id);
            return "productos/editar";
        }

        try {
            productoService.update(id, productoForm);
            redirectAttributes.addFlashAttribute("successMessage", "Producto actualizado correctamente.");
            return "redirect:/productos/listar";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Editar Producto");
            model.addAttribute("pageSubtitle", "Actualizar datos del producto");
            model.addAttribute("categorias", CATEGORIAS);
            model.addAttribute("productoId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "productos/editar";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Producto eliminado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/productos/listar";
    }
}
