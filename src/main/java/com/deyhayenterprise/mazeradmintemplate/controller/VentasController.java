package com.deyhayenterprise.mazeradmintemplate.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.deyhayenterprise.mazeradmintemplate.service.ClienteService;
import com.deyhayenterprise.mazeradmintemplate.service.ProductoService;
import com.deyhayenterprise.mazeradmintemplate.service.VentaService;
import com.deyhayenterprise.mazeradmintemplate.web.form.VentaCreateForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.VentaDetalleForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/ventas")
@RequiredArgsConstructor
@Slf4j
public class VentasController {

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final ProductoService productoService;

    @GetMapping("/nueva")
    public String nuevaVenta(Model model) {
        log.info("Nueva venta");
        loadCatalogs(model);
        model.addAttribute("pageHeading", "Nueva Venta");
        model.addAttribute("pageSubtitle", "Registrar una nueva venta");
        if (!model.containsAttribute("ventaForm")) {
            VentaCreateForm form = new VentaCreateForm();
            form.setFecha(LocalDate.now());
            model.addAttribute("ventaForm", form);
        }
        model.addAttribute("formAction", "/ventas/nueva");
        model.addAttribute("isEdit", false);
        return "ventas/nueva";
    }

    @PostMapping("/nueva")
    public String crearVenta(@Valid @ModelAttribute("ventaForm") VentaCreateForm ventaForm,
                             BindingResult bindingResult,
                             @RequestParam(name = "accion", defaultValue = "guardar") String accion,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Nueva Venta");
            model.addAttribute("pageSubtitle", "Registrar una nueva venta");
            loadCatalogs(model);
            model.addAttribute("formAction", "/ventas/nueva");
            model.addAttribute("isEdit", false);
            return "ventas/nueva";
        }

        try {
            log.info("Accion recibida en nueva venta: {}", accion);
            ventaService.create(ventaForm);
            redirectAttributes.addFlashAttribute("successMessage", "Venta registrada correctamente. Acción: " + accion);
            return "redirect:/ventas/listar";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Nueva Venta");
            model.addAttribute("pageSubtitle", "Registrar una nueva venta");
            loadCatalogs(model);
            model.addAttribute("formAction", "/ventas/nueva");
            model.addAttribute("isEdit", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "ventas/nueva";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarVenta(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var venta = ventaService.findById(id);
            VentaCreateForm form = new VentaCreateForm();
            form.setClienteId(venta.getCliente().getId());
            form.setFecha(venta.getFecha());
            for (var d : venta.getDetalles()) {
                VentaDetalleForm fd = new VentaDetalleForm();
                fd.setProductoId(d.getProducto().getId());
                fd.setCantidad(d.getCantidad());
                form.getDetalles().add(fd);
            }

            model.addAttribute("pageHeading", "Editar Venta");
            model.addAttribute("pageSubtitle", "Actualizar detalle de la venta");
            loadCatalogs(model);
            model.addAttribute("ventaForm", form);
            model.addAttribute("formAction", "/ventas/editar/" + id);
            model.addAttribute("isEdit", true);
            return "ventas/nueva";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/ventas/listar";
        }
    }

    @PostMapping("/editar/{id}")
    public String actualizarVenta(@PathVariable Long id,
                                  @Valid @ModelAttribute("ventaForm") VentaCreateForm ventaForm,
                                  BindingResult bindingResult,
                                  @RequestParam(name = "accion", defaultValue = "guardar") String accion,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Editar Venta");
            model.addAttribute("pageSubtitle", "Actualizar detalle de la venta");
            loadCatalogs(model);
            model.addAttribute("formAction", "/ventas/editar/" + id);
            model.addAttribute("isEdit", true);
            return "ventas/nueva";
        }

        try {
            log.info("Accion recibida en editar venta {}: {}", id, accion);
            ventaService.update(id, ventaForm, accion);
            redirectAttributes.addFlashAttribute("successMessage", "Venta actualizada correctamente. Acción: " + accion);
            return "redirect:/ventas/listar";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Editar Venta");
            model.addAttribute("pageSubtitle", "Actualizar detalle de la venta");
            loadCatalogs(model);
            model.addAttribute("formAction", "/ventas/editar/" + id);
            model.addAttribute("isEdit", true);
            model.addAttribute("errorMessage", ex.getMessage());
            return "ventas/nueva";
        }
    }

    @GetMapping("/listar")
    public String listarVentas(Model model) {
        log.info("Listar ventas");
        model.addAttribute("pageHeading", "Listar Ventas");
        model.addAttribute("pageSubtitle", "Historial de ventas");
        model.addAttribute("ventas", ventaService.findAll());
        return "ventas/listar";
    }

    @GetMapping("/reportes")
    public String reportesVentas(Model model) {
        log.info("Reportes de ventas");
        model.addAttribute("pageHeading", "Reportes de Ventas");
        model.addAttribute("pageSubtitle", "Análisis de ventas");
        return "ventas/reportes";
    }

    private void loadCatalogs(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("productos", productoService.findAll());
    }
}
