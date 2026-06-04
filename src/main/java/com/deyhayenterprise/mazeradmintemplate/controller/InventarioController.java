package com.deyhayenterprise.mazeradmintemplate.controller;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.deyhayenterprise.mazeradmintemplate.entity.Inventario;
import com.deyhayenterprise.mazeradmintemplate.repository.MenuOptionRepository;
import com.deyhayenterprise.mazeradmintemplate.service.InventarioService;
import com.deyhayenterprise.mazeradmintemplate.service.MovtipoService;
import com.deyhayenterprise.mazeradmintemplate.service.ProductoService;
import com.deyhayenterprise.mazeradmintemplate.web.form.InventarioCreateForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.InventarioDetalleForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/inventario")
@RequiredArgsConstructor
@Slf4j
public class InventarioController {

    private static final Set<String> LOCKED_STATUSES = Set.of("CONCLUIDO", "CANCELADO", "PENDIENTE");

    private final InventarioService inventarioService;
    private final ProductoService productoService;
    private final MovtipoService movtipoService;
    private final MenuOptionRepository menuOptionRepository;

    @GetMapping("/movimientos")
    public String listarMovimientos(Model model) {
        log.info("Listar movimientos de inventario");
        model.addAttribute("pageHeading", "Movimientos de Inventario");
        model.addAttribute("pageSubtitle", "Historial de entradas, salidas y ajustes");
        model.addAttribute("inventarios", inventarioService.findAll());
        return "inventario/movimientos";
    }

    @GetMapping("/movimientos/nuevo")
    public String nuevoMovimiento(Model model, org.springframework.security.core.Authentication authentication) {
        Set<String> allowedCodes = resolveAllowedCodes(authentication);
        loadCatalogs(model, authentication);
        applyUiFlagsForNew(model, allowedCodes);

        model.addAttribute("pageHeading", "Nuevo Movimiento de Inventario");
        model.addAttribute("pageSubtitle", "Registrar una nueva transaccion de inventario");
        if (!model.containsAttribute("inventarioForm")) {
            InventarioCreateForm form = new InventarioCreateForm();
            form.setFecha(LocalDate.now());
            model.addAttribute("inventarioForm", form);
        }
        model.addAttribute("formAction", "/inventario/movimientos/nuevo");
        model.addAttribute("isEdit", false);
        return "inventario/nueva";
    }

    @GetMapping("/movimientos/editar/{id}")
    public String editarMovimiento(@PathVariable Long id,
                                   Model model,
                                   RedirectAttributes redirectAttributes,
                                   org.springframework.security.core.Authentication authentication) {
        try {
            Set<String> allowedCodes = resolveAllowedCodes(authentication);
            Inventario inventario = inventarioService.findById(id);

            InventarioCreateForm form = new InventarioCreateForm();
            form.setFecha(inventario.getFecha());
            form.setMov(inventario.getMov());
            form.setMovid(inventario.getMovid());
            form.setComportamiento(inventario.getComportamiento());
            for (var d : inventario.getDetalles()) {
                InventarioDetalleForm fd = new InventarioDetalleForm();
                fd.setProductoId(d.getProducto().getId());
                fd.setCantidad(d.getCantidad());
                form.getDetalles().add(fd);
            }

            model.addAttribute("pageHeading", "Editar Movimiento de Inventario");
            model.addAttribute("pageSubtitle", "Actualizar transaccion de inventario");
            loadCatalogs(model, authentication);
            applyUiFlagsForEdit(model, inventario, allowedCodes);
            model.addAttribute("inventarioForm", form);
            model.addAttribute("formAction", "/inventario/movimientos/editar/" + id);
            model.addAttribute("isEdit", true);
            return "inventario/nueva";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/inventario/movimientos";
        }
    }

    @PostMapping({"/movimientos/nuevo", "/movimientos/editar/{id}"})
    public String procesarMovimiento(@PathVariable(name = "id", required = false) Long id,
                                     @Valid @ModelAttribute("inventarioForm") InventarioCreateForm inventarioForm,
                                     BindingResult bindingResult,
                                     @RequestParam(name = "accion", defaultValue = "guardar") String accion,
                                     @RequestParam(name = "comportamiento", required = false) String comportamiento,
                                     RedirectAttributes redirectAttributes,
                                     Model model,
                                     org.springframework.security.core.Authentication authentication) {
        boolean isEdit = id != null;
        Set<String> allowedCodes = resolveAllowedCodes(authentication);

        if (StringUtils.hasText(comportamiento)) {
            inventarioForm.setComportamiento(comportamiento);
        }

        validateActionPermission(accion, allowedCodes);

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", isEdit ? "Editar Movimiento de Inventario" : "Nuevo Movimiento de Inventario");
            model.addAttribute("pageSubtitle", isEdit ? "Actualizar transaccion de inventario" : "Registrar una nueva transaccion de inventario");
            loadCatalogs(model, authentication);
            applyUiFlagsForPostError(model, id, allowedCodes);
            model.addAttribute("formAction", isEdit ? "/inventario/movimientos/editar/" + id : "/inventario/movimientos/nuevo");
            model.addAttribute("isEdit", isEdit);
            return "inventario/nueva";
        }

        try {
            Inventario response = inventarioService.executeTransaction(id, inventarioForm, accion, inventarioForm.getComportamiento());
            redirectAttributes.addFlashAttribute("successMessage",
                    (isEdit ? "Movimiento actualizado" : "Movimiento registrado")
                            + " correctamente. Accion: " + accion + " - ID: " + response.getId());
            return "redirect:/inventario/movimientos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", isEdit ? "Editar Movimiento de Inventario" : "Nuevo Movimiento de Inventario");
            model.addAttribute("pageSubtitle", isEdit ? "Actualizar transaccion de inventario" : "Registrar una nueva transaccion de inventario");
            loadCatalogs(model, authentication);
            applyUiFlagsForPostError(model, id, allowedCodes);
            model.addAttribute("formAction", isEdit ? "/inventario/movimientos/editar/" + id : "/inventario/movimientos/nuevo");
            model.addAttribute("isEdit", isEdit);
            model.addAttribute("errorMessage", ex.getMessage());
            return "inventario/nueva";
        }
    }

    @GetMapping("/existencias")
    public String existencias(Model model) {
        log.info("Existencias de inventario");
        model.addAttribute("pageHeading", "Existencias");
        model.addAttribute("pageSubtitle", "Stock disponible por producto");
        model.addAttribute("productos", productoService.findAll());
        return "inventario/existencias";
    }

    @GetMapping("/ajustes")
    public String ajustes(Model model) {
        log.info("Formulario de ajustes de inventario");
        model.addAttribute("pageHeading", "Ajustes de Inventario");
        model.addAttribute("pageSubtitle", "Redirige al flujo unificado de movimientos");
        return "redirect:/inventario/movimientos/nuevo";
    }

    private void loadCatalogs(Model model, org.springframework.security.core.Authentication authentication) {
        model.addAttribute("productos", productoService.findAll());

        String username = authentication != null ? authentication.getName() : "";
        model.addAttribute("movtipos", movtipoService.findAllowedByUsernameAndModulo(username, "INV"));
    }

    private Set<String> resolveAllowedCodes(org.springframework.security.core.Authentication authentication) {
        return authentication != null
                ? menuOptionRepository.findAllowedCodesByUsername(authentication.getName()).stream()
                .map(code -> code == null ? "" : code.trim().toUpperCase())
                .collect(Collectors.toSet())
                : Set.of();
    }

    private void applyUiFlagsForNew(Model model, Set<String> allowedCodes) {
        boolean canAfectar = allowedCodes.contains("INVENTARIO_AFECTAR");
        boolean canCancelar = allowedCodes.contains("INVENTARIO_CANCELAR");
        model.addAttribute("currentStatus", "SINAFECTAR");
        model.addAttribute("isReadOnly", false);
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
            Inventario inventario = inventarioService.findById(id);
            applyUiFlagsForEdit(model, inventario, allowedCodes);
        } catch (IllegalArgumentException ex) {
            applyUiFlagsForNew(model, allowedCodes);
        }
    }

    private void applyUiFlagsForEdit(Model model, Inventario inventario, Set<String> allowedCodes) {
        String estatus = normalizeStatus(inventario);
        boolean isReadOnly = LOCKED_STATUSES.contains(estatus);
        boolean isCancelado = "CANCELADO".equals(estatus);
        boolean isConcluido = "CONCLUIDO".equals(estatus);

        boolean canAfectar = allowedCodes.contains("INVENTARIO_AFECTAR");
        boolean canCancelar = allowedCodes.contains("INVENTARIO_CANCELAR");

        boolean canAfectarAction;
        boolean canCancelarAction;
        boolean canGuardarAction;
        boolean canImprimirAction;

        if (isCancelado) {
            canAfectarAction = false;
            canCancelarAction = false;
            canGuardarAction = false;
            canImprimirAction = false;
        } else if (isConcluido) {
            canAfectarAction = false;
            canCancelarAction = false;
            canGuardarAction = false;
            canImprimirAction = true;
        } else if (isReadOnly) {
            canAfectarAction = false;
            canCancelarAction = false;
            canGuardarAction = false;
            canImprimirAction = false;
        } else {
            canAfectarAction = canAfectar;
            canCancelarAction = canCancelar;
            canGuardarAction = true;
            canImprimirAction = true;
        }

        model.addAttribute("currentStatus", estatus.isBlank() ? "SINAFECTAR" : estatus);
        model.addAttribute("isReadOnly", isReadOnly);
        model.addAttribute("canAfectarAction", canAfectarAction);
        model.addAttribute("canCancelarAction", canCancelarAction);
        model.addAttribute("canGuardarAction", canGuardarAction);
        model.addAttribute("canImprimirAction", canImprimirAction);
    }

    private String normalizeStatus(Inventario inventario) {
        String raw = StringUtils.hasText(inventario.getEstatus()) ? inventario.getEstatus() : inventario.getEstado();
        return raw == null ? "" : raw.trim().toUpperCase();
    }

    private void validateActionPermission(String accion, Set<String> allowedCodes) {
        String normalizedAction = StringUtils.hasText(accion) ? accion.trim().toLowerCase() : "";
        String requiredCode = switch (normalizedAction) {
            case "afectar" -> "INVENTARIO_AFECTAR";
            case "cancelar" -> "INVENTARIO_CANCELAR";
            default -> null;
        };

        if (requiredCode != null && !allowedCodes.contains(requiredCode)) {
            throw new IllegalArgumentException("No tienes permiso para ejecutar la accion: " + normalizedAction + ".");
        }
    }
}

