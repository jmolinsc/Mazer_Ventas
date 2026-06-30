package com.deyhayenterprise.mazeradmintemplate.controller;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.springframework.util.StringUtils;

import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.service.ClienteService;
import com.deyhayenterprise.mazeradmintemplate.service.ProductoService;
import com.deyhayenterprise.mazeradmintemplate.service.VentaService;
import com.deyhayenterprise.mazeradmintemplate.service.MovtipoService;
import com.deyhayenterprise.mazeradmintemplate.service.ReporteService;
import com.deyhayenterprise.mazeradmintemplate.web.form.VentaCreateForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.VentaDetalleForm;
import com.deyhayenterprise.mazeradmintemplate.repository.MenuOptionRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/ventas")
@RequiredArgsConstructor
@Slf4j
public class VentasController {

    private static final Set<String> LOCKED_STATUSES = Set.of("CONCLUIDO", "CANCELADO", "PENDIENTE");

    private final VentaService ventaService;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final MovtipoService movtipoService;
    private final MenuOptionRepository menuOptionRepository;
    private final ReporteService reporteService;

    @GetMapping("/nueva")
    public String nuevaVenta(Model model, org.springframework.security.core.Authentication authentication) {
        log.info("Nueva venta");
        Set<String> allowedCodes = resolveAllowedCodes(authentication);
        loadCatalogs(model);
        applyUiFlagsForNew(model, allowedCodes);

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

    @PostMapping({"/nueva", "/editar/{id}"})
    public String procesarVenta(@PathVariable(name = "id", required = false) Long id,
                                @Valid @ModelAttribute("ventaForm") VentaCreateForm ventaForm,
                                BindingResult bindingResult,
                                @RequestParam(name = "accion", defaultValue = "guardar") String accion,
                                @RequestParam(name = "comportamiento", required = false) String comportamiento,
                                RedirectAttributes redirectAttributes,
                                Model model,
                                org.springframework.security.core.Authentication authentication) {
        boolean isEdit = id != null;
        Set<String> allowedCodes = resolveAllowedCodes(authentication);

        if (StringUtils.hasText(comportamiento)) {
            ventaForm.setComportamiento(comportamiento);
        }

        validateActionPermission(accion, allowedCodes);

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", isEdit ? "Editar Venta" : "Nueva Venta");
            model.addAttribute("pageSubtitle", isEdit ? "Actualizar detalle de la venta" : "Registrar una nueva venta");
            loadCatalogs(model);
            applyUiFlagsForPostError(model, id, allowedCodes);
            model.addAttribute("formAction", isEdit ? "/ventas/editar/" + id : "/ventas/nueva");
            model.addAttribute("isEdit", isEdit);
            return "ventas/nueva";
        }

        try {
            log.info("Accion recibida en venta {}: {}", isEdit ? id : "NUEVA", accion);
            Venta response = ventaService.executeTransaction(id, ventaForm, accion, ventaForm.getComportamiento());
            redirectAttributes.addFlashAttribute("successMessage",
                    (isEdit ? "Venta actualizada" : "Venta registrada")
                            + " correctamente. Acción: " + accion + " - ID: " + response.getId());
            return "redirect:/ventas/listar";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", isEdit ? "Editar Venta" : "Nueva Venta");
            model.addAttribute("pageSubtitle", isEdit ? "Actualizar detalle de la venta" : "Registrar una nueva venta");
            loadCatalogs(model);
            applyUiFlagsForPostError(model, id, allowedCodes);
            model.addAttribute("formAction", isEdit ? "/ventas/editar/" + id : "/ventas/nueva");
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("errorMessage", ex.getMessage());
            return "ventas/nueva";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarVenta(@PathVariable Long id,
                          Model model,
                          RedirectAttributes redirectAttributes,
                          org.springframework.security.core.Authentication authentication) {
        try {
            Set<String> allowedCodes = resolveAllowedCodes(authentication);
            var venta = ventaService.findById(id);
            VentaCreateForm form = new VentaCreateForm();
            form.setClienteId(venta.getCliente().getId());
            form.setFecha(venta.getFecha());
            form.setMov(venta.getMov());
            form.setMovid(venta.getMovid());
            form.setComportamiento(venta.getComportamiento());
            for (var d : venta.getDetalles()) {
                VentaDetalleForm fd = new VentaDetalleForm();
                fd.setProductoId(d.getProducto().getId());
                fd.setCantidad(d.getCantidad());
                form.getDetalles().add(fd);
            }

            model.addAttribute("pageHeading", "Editar Venta");
            model.addAttribute("pageSubtitle", "Actualizar detalle de la venta");
            loadCatalogs(model);
            applyUiFlagsForEdit(model, venta, allowedCodes);
            model.addAttribute("ventaForm", form);
            model.addAttribute("formAction", "/ventas/editar/" + id);
            model.addAttribute("formActionImprimir", "/reportes/venta/imprimir/" + id);
            model.addAttribute("isEdit", true);
            return "ventas/nueva";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/ventas/listar";
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
        model.addAttribute("ventas", ventaService.findAll());
        return "ventas/reportes";
    }




    private void loadCatalogs(Model model) {
        model.addAttribute("clientes", clienteService.findAll());
        model.addAttribute("productos", productoService.findAll());

        var movtiposVenta = movtipoService.findAllActive().stream()
                .filter(m -> m.getComportamiento() != null
                        && m.getComportamiento().getModulo() != null
                        && "VTA".equals(m.getComportamiento().getModulo().getNombre()))
                .toList();
        model.addAttribute("movtipos", movtiposVenta);
    }

    private Set<String> resolveAllowedCodes(org.springframework.security.core.Authentication authentication) {
        return authentication != null
                ? menuOptionRepository.findAllowedCodesByUsername(authentication.getName()).stream()
                .map(code -> code == null ? "" : code.trim().toUpperCase())
                .collect(Collectors.toSet())
                : Set.of();
    }

    private void applyUiFlagsForNew(Model model, Set<String> allowedCodes) {
        boolean canAfectar = allowedCodes.contains("VENTAS_AFECTAR");
        boolean canCancelar = allowedCodes.contains("VENTAS_CANCELAR");
        model.addAttribute("currentStatus", "SINAFECTAR");
        model.addAttribute("isReadOnly", false);
        model.addAttribute("canAfectar", canAfectar);
        model.addAttribute("canCancelar", canCancelar);
        model.addAttribute("canAfectarAction", canAfectar);
        model.addAttribute("canCancelarAction", canCancelar);
        model.addAttribute("canGuardarAction", true);
        model.addAttribute("canImprimirAction", true);
    }

    private void applyUiFlagsForPostError(Model model, Long id, Set<String> allowedCodes) {
        if (id == null) {
            applyUiFlagsForNew(model, allowedCodes);
            return;
        }
        try {
            Venta venta = ventaService.findById(id);
            applyUiFlagsForEdit(model, venta, allowedCodes);
        } catch (IllegalArgumentException ex) {
            applyUiFlagsForNew(model, allowedCodes);
        }
    }

    private void applyUiFlagsForEdit(Model model, Venta venta, Set<String> allowedCodes) {
        String estatus = normalizeStatus(venta);
        String comportamiento = venta.getComportamiento() == null ? "" : venta.getComportamiento().trim().toUpperCase();
        boolean isReadOnly = LOCKED_STATUSES.contains(estatus);
        boolean isPedidoPendiente = "PEDIDO".equals(comportamiento) && "PENDIENTE".equals(estatus);
        boolean isCancelado = "CANCELADO".equals(estatus);
        boolean isConcluido = "CONCLUIDO".equals(estatus);

        boolean canAfectar = allowedCodes.contains("VENTAS_AFECTAR");
        boolean canCancelar = allowedCodes.contains("VENTAS_CANCELAR");

        boolean canAfectarAction;
        boolean canCancelarAction;
        boolean canGuardarAction;
        boolean canImprimirAction;

        if (isCancelado) {
            // Cancelado: formulario totalmente apagado sin excepciones.
            canAfectarAction = false;
            canCancelarAction = false;
            canGuardarAction = false;
            canImprimirAction = false;
        } else if (isConcluido) {
            // Concluido: solo impresion habilitada.
            canAfectarAction = false;
            canCancelarAction = false;
            canGuardarAction = false;
            canImprimirAction = true;
        } else if (isPedidoPendiente) {
            // Pedido pendiente: permite afectar/cancelar segun permisos.
            canAfectarAction = canAfectar;
            canCancelarAction = canCancelar;
            canGuardarAction = false;
            canImprimirAction = false;
        } else if (isReadOnly) {
            // Cualquier otro estado bloqueado no permite acciones.
            canAfectarAction = false;
            canCancelarAction = false;
            canGuardarAction = false;
            canImprimirAction = false;
        } else {
            // Estado editable normal.
            canAfectarAction = canAfectar;
            canCancelarAction = canCancelar;
            canGuardarAction = true;
            canImprimirAction = true;
        }

        model.addAttribute("currentStatus", estatus.isBlank() ? "SINAFECTAR" : estatus);
        model.addAttribute("isReadOnly", isReadOnly);
        model.addAttribute("canAfectar", canAfectar);
        model.addAttribute("canCancelar", canCancelar);
        model.addAttribute("canAfectarAction", canAfectarAction);
        model.addAttribute("canCancelarAction", canCancelarAction);
        model.addAttribute("canGuardarAction", canGuardarAction);
        model.addAttribute("canImprimirAction", canImprimirAction);
    }

    private String normalizeStatus(Venta venta) {
        String raw = StringUtils.hasText(venta.getEstatus()) ? venta.getEstatus() : venta.getEstado();
        return raw == null ? "" : raw.trim().toUpperCase();
    }

    private void validateActionPermission(String accion, Set<String> allowedCodes) {
        String normalizedAction = StringUtils.hasText(accion) ? accion.trim().toLowerCase() : "";
        String requiredCode = switch (normalizedAction) {
            case "afectar" -> "VENTAS_AFECTAR";
            case "cancelar" -> "VENTAS_CANCELAR";
            default -> null;
        };

        if (requiredCode != null && !allowedCodes.contains(requiredCode)) {
            throw new IllegalArgumentException("No tienes permiso para ejecutar la accion: " + normalizedAction + ".");
        }
    }
}
