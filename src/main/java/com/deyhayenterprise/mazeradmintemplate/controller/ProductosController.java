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
import com.deyhayenterprise.mazeradmintemplate.service.ProductoCategoriaService;
import com.deyhayenterprise.mazeradmintemplate.service.ProductoService;
import com.deyhayenterprise.mazeradmintemplate.service.UnidadMedidaService;
import com.deyhayenterprise.mazeradmintemplate.web.form.ProductoCategoriaForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.ProductoCreateForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.UnidadMedidaForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
@Slf4j
public class ProductosController {

    private final ProductoService productoService;
    private final ProductoCategoriaService productoCategoriaService;
    private final FabricanteService fabricanteService;
    private final UnidadMedidaService unidadMedidaService;

    @GetMapping("/nuevo")
    public String nuevoProducto(Model model) {
        log.info("Nuevo producto");
        model.addAttribute("pageHeading", "Nuevo Producto");
        model.addAttribute("pageSubtitle", "Registrar un nuevo producto");
        model.addAttribute("categorias", productoCategoriaService.findAllCategoryNames());
        model.addAttribute("fabricantes", fabricanteService.findAll());
        model.addAttribute("unidades", unidadMedidaService.findAllNames());
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
            model.addAttribute("categorias", productoCategoriaService.findAllCategoryNames());
            model.addAttribute("fabricantes", fabricanteService.findAll());
            model.addAttribute("unidades", unidadMedidaService.findAllNames());
            return "productos/nuevo";
        }

        try {
            productoService.create(productoForm);
            redirectAttributes.addFlashAttribute("successMessage", "Producto creado correctamente.");
            return "redirect:/productos/listar";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Nuevo Producto");
            model.addAttribute("pageSubtitle", "Registrar un nuevo producto");
            model.addAttribute("categorias", productoCategoriaService.findAllCategoryNames());
            model.addAttribute("fabricantes", fabricanteService.findAll());
            model.addAttribute("unidades", unidadMedidaService.findAllNames());
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
        model.addAttribute("categorias", productoCategoriaService.findAllActive());
        if (!model.containsAttribute("categoriaForm")) {
            model.addAttribute("categoriaForm", new ProductoCategoriaForm());
        }
        model.addAttribute("editMode", false);
        return "productos/categorias";
    }

    @PostMapping("/categorias")
    public String crearCategoria(@Valid @ModelAttribute("categoriaForm") ProductoCategoriaForm categoriaForm,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Categorías de Productos");
            model.addAttribute("pageSubtitle", "Administrar categorías");
            model.addAttribute("categorias", productoCategoriaService.findAllActive());
            model.addAttribute("editMode", false);
            return "productos/categorias";
        }
        try {
            productoCategoriaService.create(categoriaForm);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría creada correctamente.");
            return "redirect:/productos/categorias";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Categorías de Productos");
            model.addAttribute("pageSubtitle", "Administrar categorías");
            model.addAttribute("categorias", productoCategoriaService.findAllActive());
            model.addAttribute("editMode", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "productos/categorias";
        }
    }

    @GetMapping("/categorias/editar/{id}")
    public String editarCategoria(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var categoria = productoCategoriaService.findActiveById(id);
            ProductoCategoriaForm form = new ProductoCategoriaForm();
            form.setNombre(categoria.getNombre());
            form.setDescripcion(categoria.getDescripcion());

            model.addAttribute("pageHeading", "Categorías de Productos");
            model.addAttribute("pageSubtitle", "Administrar categorías");
            model.addAttribute("categorias", productoCategoriaService.findAllActive());
            model.addAttribute("categoriaForm", form);
            model.addAttribute("editMode", true);
            model.addAttribute("categoriaId", id);
            return "productos/categorias";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/productos/categorias";
        }
    }

    @PostMapping("/categorias/editar/{id}")
    public String actualizarCategoria(@PathVariable Long id,
                                      @Valid @ModelAttribute("categoriaForm") ProductoCategoriaForm categoriaForm,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Categorías de Productos");
            model.addAttribute("pageSubtitle", "Administrar categorías");
            model.addAttribute("categorias", productoCategoriaService.findAllActive());
            model.addAttribute("editMode", true);
            model.addAttribute("categoriaId", id);
            return "productos/categorias";
        }
        try {
            productoCategoriaService.update(id, categoriaForm);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría actualizada correctamente.");
            return "redirect:/productos/categorias";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Categorías de Productos");
            model.addAttribute("pageSubtitle", "Administrar categorías");
            model.addAttribute("categorias", productoCategoriaService.findAllActive());
            model.addAttribute("editMode", true);
            model.addAttribute("categoriaId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "productos/categorias";
        }
    }

    @PostMapping("/categorias/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productoCategoriaService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Categoría eliminada correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/productos/categorias";
    }

    @GetMapping("/categorias/unidades")
    public String unidades(Model model) {
        model.addAttribute("pageHeading", "Unidades de Medida");
        model.addAttribute("pageSubtitle", "Administrar unidades para productos");
        model.addAttribute("unidades", unidadMedidaService.findAllActive());
        if (!model.containsAttribute("unidadForm")) {
            model.addAttribute("unidadForm", new UnidadMedidaForm());
        }
        model.addAttribute("editMode", false);
        return "productos/unidades";
    }

    @PostMapping("/categorias/unidades")
    public String crearUnidad(@Valid @ModelAttribute("unidadForm") UnidadMedidaForm unidadForm,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Unidades de Medida");
            model.addAttribute("pageSubtitle", "Administrar unidades para productos");
            model.addAttribute("unidades", unidadMedidaService.findAllActive());
            model.addAttribute("editMode", false);
            return "productos/unidades";
        }

        try {
            unidadMedidaService.create(unidadForm);
            redirectAttributes.addFlashAttribute("successMessage", "Unidad creada correctamente.");
            return "redirect:/productos/categorias/unidades";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Unidades de Medida");
            model.addAttribute("pageSubtitle", "Administrar unidades para productos");
            model.addAttribute("unidades", unidadMedidaService.findAllActive());
            model.addAttribute("editMode", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "productos/unidades";
        }
    }

    @GetMapping("/categorias/unidades/editar/{id}")
    public String editarUnidad(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var unidad = unidadMedidaService.findActiveById(id);
            UnidadMedidaForm form = new UnidadMedidaForm();
            form.setNombre(unidad.getNombre());
            form.setDescripcion(unidad.getDescripcion());

            model.addAttribute("pageHeading", "Unidades de Medida");
            model.addAttribute("pageSubtitle", "Administrar unidades para productos");
            model.addAttribute("unidades", unidadMedidaService.findAllActive());
            model.addAttribute("unidadForm", form);
            model.addAttribute("editMode", true);
            model.addAttribute("unidadId", id);
            return "productos/unidades";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/productos/categorias/unidades";
        }
    }

    @PostMapping("/categorias/unidades/editar/{id}")
    public String actualizarUnidad(@PathVariable Long id,
                                   @Valid @ModelAttribute("unidadForm") UnidadMedidaForm unidadForm,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Unidades de Medida");
            model.addAttribute("pageSubtitle", "Administrar unidades para productos");
            model.addAttribute("unidades", unidadMedidaService.findAllActive());
            model.addAttribute("editMode", true);
            model.addAttribute("unidadId", id);
            return "productos/unidades";
        }

        try {
            unidadMedidaService.update(id, unidadForm);
            redirectAttributes.addFlashAttribute("successMessage", "Unidad actualizada correctamente.");
            return "redirect:/productos/categorias/unidades";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Unidades de Medida");
            model.addAttribute("pageSubtitle", "Administrar unidades para productos");
            model.addAttribute("unidades", unidadMedidaService.findAllActive());
            model.addAttribute("editMode", true);
            model.addAttribute("unidadId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "productos/unidades";
        }
    }

    @PostMapping("/categorias/unidades/eliminar/{id}")
    public String eliminarUnidad(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            unidadMedidaService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Unidad eliminada correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/productos/categorias/unidades";
    }

    @GetMapping("/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("pageHeading", "Editar Producto");
            model.addAttribute("pageSubtitle", "Actualizar datos del producto");
            model.addAttribute("categorias", productoCategoriaService.findAllCategoryNames());
            model.addAttribute("fabricantes", fabricanteService.findAll());
            model.addAttribute("unidades", unidadMedidaService.findAllNames());
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
                form.setFabricanteId(producto.getFabricante().getId());
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
            model.addAttribute("categorias", productoCategoriaService.findAllCategoryNames());
            model.addAttribute("fabricantes", fabricanteService.findAll());
            model.addAttribute("unidades", unidadMedidaService.findAllNames());
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
            model.addAttribute("categorias", productoCategoriaService.findAllCategoryNames());
            model.addAttribute("fabricantes", fabricanteService.findAll());
            model.addAttribute("unidades", unidadMedidaService.findAllNames());
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
