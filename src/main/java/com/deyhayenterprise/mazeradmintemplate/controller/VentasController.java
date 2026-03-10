package com.deyhayenterprise.mazeradmintemplate.controller;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.deyhayenterprise.mazeradmintemplate.service.ClienteService;
import com.deyhayenterprise.mazeradmintemplate.service.ProductoService;
import com.deyhayenterprise.mazeradmintemplate.service.VentaService;
import com.deyhayenterprise.mazeradmintemplate.web.form.VentaCreateForm;

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
        model.addAttribute("pageHeading", "Nueva Venta");
        model.addAttribute("pageSubtitle", "Registrar una nueva venta");
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("productos", productoService.findAll());
        if (!model.containsAttribute("ventaForm")) {
            VentaCreateForm form = new VentaCreateForm();
            form.setFecha(LocalDate.now());
            form.setCantidad(1);
            model.addAttribute("ventaForm", form);
        }
        return "ventas/nueva";
    }

    @PostMapping("/nueva")
    public String crearVenta(@Valid @ModelAttribute("ventaForm") VentaCreateForm ventaForm,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Nueva Venta");
            model.addAttribute("pageSubtitle", "Registrar una nueva venta");
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("productos", productoService.findAll());
            return "ventas/nueva";
        }

        try {
            ventaService.create(ventaForm);
            redirectAttributes.addFlashAttribute("successMessage", "Venta registrada correctamente.");
            return "redirect:/ventas/listar";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Nueva Venta");
            model.addAttribute("pageSubtitle", "Registrar una nueva venta");
            model.addAttribute("clientes", clienteService.findAll());
            model.addAttribute("productos", productoService.findAll());
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
}
