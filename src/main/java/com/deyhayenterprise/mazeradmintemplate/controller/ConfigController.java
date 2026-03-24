package com.deyhayenterprise.mazeradmintemplate.controller;

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

import com.deyhayenterprise.mazeradmintemplate.service.AppUserService;
import com.deyhayenterprise.mazeradmintemplate.service.ComportamientoService;
import com.deyhayenterprise.mazeradmintemplate.service.MenuAdminService;
import com.deyhayenterprise.mazeradmintemplate.service.ModuloService;
import com.deyhayenterprise.mazeradmintemplate.service.MovtipoService;
import com.deyhayenterprise.mazeradmintemplate.service.RoleService;
import com.deyhayenterprise.mazeradmintemplate.web.form.AppMenuForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.ComportamientoForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.MenuOptionForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.ModuloForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.MovtipoForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.RoleCreateForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.RolePermissionUpdateForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.UserCreateForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/config")
@RequiredArgsConstructor
@Slf4j
public class ConfigController {

    private final AppUserService appUserService;
    private final RoleService roleService;
    private final MenuAdminService menuAdminService;
    private final ModuloService moduloService;
    private final ComportamientoService comportamientoService;
    private final MovtipoService movtipoService;

    @GetMapping("/empresa")
    public String empresa(Model model) {
        log.info("Configuración de empresa");
        model.addAttribute("pageHeading", "Datos de la Empresa");
        model.addAttribute("pageSubtitle", "Información general de la empresa");
        return "config/empresa";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        log.info("Gestión de usuarios");
        model.addAttribute("pageHeading", "Gestión de Usuarios");
        model.addAttribute("pageSubtitle", "Administrar usuarios del sistema");
        model.addAttribute("users", appUserService.findAllUsers());
        model.addAttribute("roles", roleService.findAllRoles());
        if (!model.containsAttribute("userForm")) {
            model.addAttribute("userForm", new UserCreateForm());
        }
        return "config/usuarios";
    }

    @PostMapping("/usuarios")
    public String crearUsuario(@Valid @ModelAttribute("userForm") UserCreateForm userForm,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Gestión de Usuarios");
            model.addAttribute("pageSubtitle", "Administrar usuarios del sistema");
            model.addAttribute("users", appUserService.findAllUsers());
            model.addAttribute("roles", roleService.findAllRoles());
            return "config/usuarios";
        }

        try {
            appUserService.createUser(userForm);
            redirectAttributes.addFlashAttribute("successMessage", "Usuario creado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userForm", bindingResult);
            redirectAttributes.addFlashAttribute("userForm", userForm);
        }
        return "redirect:/config/usuarios";
    }

    @GetMapping("/permisos")
    public String permisos(@RequestParam(name = "roleId", required = false) Long roleId, Model model) {
        log.info("Gestión de permisos");
        Long selectedRoleId = resolveRoleId(roleId);

        model.addAttribute("pageHeading", "Gestión de Permisos");
        model.addAttribute("pageSubtitle", "Configurar permisos de usuarios por rol");
        model.addAttribute("roles", roleService.findAllRoles());
        model.addAttribute("permissionCatalog", roleService.buildPermissionCatalog());
        model.addAttribute("selectedRoleId", selectedRoleId);
        model.addAttribute("selectedOptionIds", roleService.findAssignedOptionIds(selectedRoleId));
        if (!model.containsAttribute("roleForm")) {
            model.addAttribute("roleForm", new RoleCreateForm());
        }
        if (!model.containsAttribute("permissionForm")) {
            RolePermissionUpdateForm form = new RolePermissionUpdateForm();
            form.setOptionIds(roleService.findAssignedOptionIds(selectedRoleId));
            model.addAttribute("permissionForm", form);
        }
        return "config/permisos";
    }

    @PostMapping("/permisos/roles")
    public String crearRol(@Valid @ModelAttribute("roleForm") RoleCreateForm roleForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Gestión de Permisos");
            model.addAttribute("pageSubtitle", "Configurar permisos de usuarios por rol");
            model.addAttribute("roles", roleService.findAllRoles());
            model.addAttribute("permissionCatalog", roleService.buildPermissionCatalog());
            model.addAttribute("selectedRoleId", resolveRoleId(null));
            model.addAttribute("selectedOptionIds", roleService.findAssignedOptionIds(resolveRoleId(null)));
            if (!model.containsAttribute("permissionForm")) {
                model.addAttribute("permissionForm", new RolePermissionUpdateForm());
            }
            return "config/permisos";
        }

        try {
            roleService.createRole(roleForm.getCodigo(), roleForm.getNombre(), roleForm.getDescripcion());
            redirectAttributes.addFlashAttribute("successMessage", "Rol creado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.roleForm", bindingResult);
            redirectAttributes.addFlashAttribute("roleForm", roleForm);
        }

        return "redirect:/config/permisos";
    }

    @PostMapping("/permisos/{roleId}")
    public String actualizarPermisos(@PathVariable Long roleId,
                                     @ModelAttribute("permissionForm") RolePermissionUpdateForm permissionForm,
                                     RedirectAttributes redirectAttributes) {
        roleService.updateRolePermissions(roleId, permissionForm.getOptionIds());
        redirectAttributes.addFlashAttribute("successMessage", "Permisos actualizados correctamente.");
        return "redirect:/config/permisos?roleId=" + roleId;
    }

    @GetMapping("/menus")
    public String menus(Model model) {
        log.info("Gestión de menús");
        model.addAttribute("pageHeading", "Gestión de Menús");
        model.addAttribute("pageSubtitle", "Crear menús y asociar URLs");
        model.addAttribute("menus", menuAdminService.findAllMenus());
        if (!model.containsAttribute("menuForm")) {
            AppMenuForm menuForm = new AppMenuForm();
            menuForm.setOrdenVisual(0);
            model.addAttribute("menuForm", menuForm);
        }
        if (!model.containsAttribute("menuOptionForm")) {
            MenuOptionForm optionForm = new MenuOptionForm();
            optionForm.setOrdenVisual(0);
            model.addAttribute("menuOptionForm", optionForm);
        }
        return "config/menus";
    }

    @PostMapping("/menus")
    public String crearMenu(@Valid @ModelAttribute("menuForm") AppMenuForm menuForm,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Gestión de Menús");
            model.addAttribute("pageSubtitle", "Crear menús y asociar URLs");
            model.addAttribute("menus", menuAdminService.findAllMenus());
            if (!model.containsAttribute("menuOptionForm")) {
                MenuOptionForm optionForm = new MenuOptionForm();
                optionForm.setOrdenVisual(0);
                model.addAttribute("menuOptionForm", optionForm);
            }
            return "config/menus";
        }

        try {
            menuAdminService.createMenu(menuForm);
            redirectAttributes.addFlashAttribute("successMessage", "Menú creado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.menuForm", bindingResult);
            redirectAttributes.addFlashAttribute("menuForm", menuForm);
        }
        return "redirect:/config/menus";
    }

    @PostMapping("/menus/opciones")
    public String crearOpcion(@Valid @ModelAttribute("menuOptionForm") MenuOptionForm menuOptionForm,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Gestión de Menús");
            model.addAttribute("pageSubtitle", "Crear menús y asociar URLs");
            model.addAttribute("menus", menuAdminService.findAllMenus());
            if (!model.containsAttribute("menuForm")) {
                AppMenuForm menuForm = new AppMenuForm();
                menuForm.setOrdenVisual(0);
                model.addAttribute("menuForm", menuForm);
            }
            return "config/menus";
        }

        try {
            menuAdminService.createOption(menuOptionForm);
            redirectAttributes.addFlashAttribute("successMessage", "Opción de menú creada correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.menuOptionForm", bindingResult);
            redirectAttributes.addFlashAttribute("menuOptionForm", menuOptionForm);
        }
        return "redirect:/config/menus";
    }

    @GetMapping("/movimientos/modulos")
    public String modulos(Model model) {
        model.addAttribute("pageHeading", "Movimientos - Módulos");
        model.addAttribute("pageSubtitle", "Administrar módulos de documentos");
        model.addAttribute("modulos", moduloService.findAllActive());
        if (!model.containsAttribute("moduloForm")) {
            model.addAttribute("moduloForm", new ModuloForm());
        }
        model.addAttribute("editMode", false);
        return "config/movimientos-modulos";
    }

    @PostMapping("/movimientos/modulos")
    public String crearModulo(@Valid @ModelAttribute("moduloForm") ModuloForm moduloForm,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Movimientos - Módulos");
            model.addAttribute("pageSubtitle", "Administrar módulos de documentos");
            model.addAttribute("modulos", moduloService.findAllActive());
            model.addAttribute("editMode", false);
            return "config/movimientos-modulos";
        }
        try {
            moduloService.create(moduloForm);
            redirectAttributes.addFlashAttribute("successMessage", "Módulo creado correctamente.");
            return "redirect:/config/movimientos/modulos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Movimientos - Módulos");
            model.addAttribute("pageSubtitle", "Administrar módulos de documentos");
            model.addAttribute("modulos", moduloService.findAllActive());
            model.addAttribute("editMode", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "config/movimientos-modulos";
        }
    }

    @GetMapping("/movimientos/modulos/editar/{id}")
    public String editarModulo(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var modulo = moduloService.findActiveById(id);
            ModuloForm form = new ModuloForm();
            form.setNombre(modulo.getNombre());
            model.addAttribute("pageHeading", "Movimientos - Módulos");
            model.addAttribute("pageSubtitle", "Administrar módulos de documentos");
            model.addAttribute("modulos", moduloService.findAllActive());
            model.addAttribute("moduloForm", form);
            model.addAttribute("editMode", true);
            model.addAttribute("moduloId", id);
            return "config/movimientos-modulos";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/config/movimientos/modulos";
        }
    }

    @PostMapping("/movimientos/modulos/editar/{id}")
    public String actualizarModulo(@PathVariable Long id,
                                   @Valid @ModelAttribute("moduloForm") ModuloForm moduloForm,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Movimientos - Módulos");
            model.addAttribute("pageSubtitle", "Administrar módulos de documentos");
            model.addAttribute("modulos", moduloService.findAllActive());
            model.addAttribute("editMode", true);
            model.addAttribute("moduloId", id);
            return "config/movimientos-modulos";
        }
        try {
            moduloService.update(id, moduloForm);
            redirectAttributes.addFlashAttribute("successMessage", "Módulo actualizado correctamente.");
            return "redirect:/config/movimientos/modulos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Movimientos - Módulos");
            model.addAttribute("pageSubtitle", "Administrar módulos de documentos");
            model.addAttribute("modulos", moduloService.findAllActive());
            model.addAttribute("editMode", true);
            model.addAttribute("moduloId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "config/movimientos-modulos";
        }
    }

    @PostMapping("/movimientos/modulos/eliminar/{id}")
    public String eliminarModulo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            moduloService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Módulo eliminado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/config/movimientos/modulos";
    }

    @GetMapping("/movimientos/comportamientos")
    public String comportamientos(Model model) {
        model.addAttribute("pageHeading", "Movimientos - Comportamientos");
        model.addAttribute("pageSubtitle", "Administrar comportamientos por módulo");
        model.addAttribute("comportamientos", comportamientoService.findAllActive());
        model.addAttribute("modulos", moduloService.findAllActive());
        if (!model.containsAttribute("comportamientoForm")) {
            model.addAttribute("comportamientoForm", new ComportamientoForm());
        }
        model.addAttribute("editMode", false);
        return "config/movimientos-comportamientos";
    }

    @PostMapping("/movimientos/comportamientos")
    public String crearComportamiento(@Valid @ModelAttribute("comportamientoForm") ComportamientoForm comportamientoForm,
                                      BindingResult bindingResult,
                                      RedirectAttributes redirectAttributes,
                                      Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Movimientos - Comportamientos");
            model.addAttribute("pageSubtitle", "Administrar comportamientos por módulo");
            model.addAttribute("comportamientos", comportamientoService.findAllActive());
            model.addAttribute("modulos", moduloService.findAllActive());
            model.addAttribute("editMode", false);
            return "config/movimientos-comportamientos";
        }
        try {
            comportamientoService.create(comportamientoForm);
            redirectAttributes.addFlashAttribute("successMessage", "Comportamiento creado correctamente.");
            return "redirect:/config/movimientos/comportamientos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Movimientos - Comportamientos");
            model.addAttribute("pageSubtitle", "Administrar comportamientos por módulo");
            model.addAttribute("comportamientos", comportamientoService.findAllActive());
            model.addAttribute("modulos", moduloService.findAllActive());
            model.addAttribute("editMode", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "config/movimientos-comportamientos";
        }
    }

    @GetMapping("/movimientos/comportamientos/editar/{id}")
    public String editarComportamiento(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var item = comportamientoService.findActiveById(id);
            ComportamientoForm form = new ComportamientoForm();
            form.setNombre(item.getNombre());
            form.setModuloId(item.getModulo().getId());
            model.addAttribute("pageHeading", "Movimientos - Comportamientos");
            model.addAttribute("pageSubtitle", "Administrar comportamientos por módulo");
            model.addAttribute("comportamientos", comportamientoService.findAllActive());
            model.addAttribute("modulos", moduloService.findAllActive());
            model.addAttribute("comportamientoForm", form);
            model.addAttribute("editMode", true);
            model.addAttribute("comportamientoId", id);
            return "config/movimientos-comportamientos";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/config/movimientos/comportamientos";
        }
    }

    @PostMapping("/movimientos/comportamientos/editar/{id}")
    public String actualizarComportamiento(@PathVariable Long id,
                                           @Valid @ModelAttribute("comportamientoForm") ComportamientoForm comportamientoForm,
                                           BindingResult bindingResult,
                                           RedirectAttributes redirectAttributes,
                                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Movimientos - Comportamientos");
            model.addAttribute("pageSubtitle", "Administrar comportamientos por módulo");
            model.addAttribute("comportamientos", comportamientoService.findAllActive());
            model.addAttribute("modulos", moduloService.findAllActive());
            model.addAttribute("editMode", true);
            model.addAttribute("comportamientoId", id);
            return "config/movimientos-comportamientos";
        }
        try {
            comportamientoService.update(id, comportamientoForm);
            redirectAttributes.addFlashAttribute("successMessage", "Comportamiento actualizado correctamente.");
            return "redirect:/config/movimientos/comportamientos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Movimientos - Comportamientos");
            model.addAttribute("pageSubtitle", "Administrar comportamientos por módulo");
            model.addAttribute("comportamientos", comportamientoService.findAllActive());
            model.addAttribute("modulos", moduloService.findAllActive());
            model.addAttribute("editMode", true);
            model.addAttribute("comportamientoId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "config/movimientos-comportamientos";
        }
    }

    @PostMapping("/movimientos/comportamientos/eliminar/{id}")
    public String eliminarComportamiento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            comportamientoService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Comportamiento eliminado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/config/movimientos/comportamientos";
    }

    @GetMapping("/movimientos/movtipos")
    public String movtipos(Model model) {
        model.addAttribute("pageHeading", "Movimientos - Tipos");
        model.addAttribute("pageSubtitle", "Administrar tipos de movimiento");
        model.addAttribute("movtipos", movtipoService.findAllActive());
        model.addAttribute("comportamientos", comportamientoService.findAllActive());
        if (!model.containsAttribute("movtipoForm")) {
            model.addAttribute("movtipoForm", new MovtipoForm());
        }
        model.addAttribute("editMode", false);
        return "config/movimientos-movtipos";
    }

    @PostMapping("/movimientos/movtipos")
    public String crearMovtipo(@Valid @ModelAttribute("movtipoForm") MovtipoForm movtipoForm,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Movimientos - Tipos");
            model.addAttribute("pageSubtitle", "Administrar tipos de movimiento");
            model.addAttribute("movtipos", movtipoService.findAllActive());
            model.addAttribute("comportamientos", comportamientoService.findAllActive());
            model.addAttribute("editMode", false);
            return "config/movimientos-movtipos";
        }
        try {
            movtipoService.create(movtipoForm);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de movimiento creado correctamente.");
            return "redirect:/config/movimientos/movtipos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Movimientos - Tipos");
            model.addAttribute("pageSubtitle", "Administrar tipos de movimiento");
            model.addAttribute("movtipos", movtipoService.findAllActive());
            model.addAttribute("comportamientos", comportamientoService.findAllActive());
            model.addAttribute("editMode", false);
            model.addAttribute("errorMessage", ex.getMessage());
            return "config/movimientos-movtipos";
        }
    }

    @GetMapping("/movimientos/movtipos/editar/{id}")
    public String editarMovtipo(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            var item = movtipoService.findActiveById(id);
            MovtipoForm form = new MovtipoForm();
            form.setNombre(item.getNombre());
            form.setComportamientoId(item.getComportamiento().getId());
            model.addAttribute("pageHeading", "Movimientos - Tipos");
            model.addAttribute("pageSubtitle", "Administrar tipos de movimiento");
            model.addAttribute("movtipos", movtipoService.findAllActive());
            model.addAttribute("comportamientos", comportamientoService.findAllActive());
            model.addAttribute("movtipoForm", form);
            model.addAttribute("editMode", true);
            model.addAttribute("movtipoId", id);
            return "config/movimientos-movtipos";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/config/movimientos/movtipos";
        }
    }

    @PostMapping("/movimientos/movtipos/editar/{id}")
    public String actualizarMovtipo(@PathVariable Long id,
                                    @Valid @ModelAttribute("movtipoForm") MovtipoForm movtipoForm,
                                    BindingResult bindingResult,
                                    RedirectAttributes redirectAttributes,
                                    Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageHeading", "Movimientos - Tipos");
            model.addAttribute("pageSubtitle", "Administrar tipos de movimiento");
            model.addAttribute("movtipos", movtipoService.findAllActive());
            model.addAttribute("comportamientos", comportamientoService.findAllActive());
            model.addAttribute("editMode", true);
            model.addAttribute("movtipoId", id);
            return "config/movimientos-movtipos";
        }
        try {
            movtipoService.update(id, movtipoForm);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de movimiento actualizado correctamente.");
            return "redirect:/config/movimientos/movtipos";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("pageHeading", "Movimientos - Tipos");
            model.addAttribute("pageSubtitle", "Administrar tipos de movimiento");
            model.addAttribute("movtipos", movtipoService.findAllActive());
            model.addAttribute("comportamientos", comportamientoService.findAllActive());
            model.addAttribute("editMode", true);
            model.addAttribute("movtipoId", id);
            model.addAttribute("errorMessage", ex.getMessage());
            return "config/movimientos-movtipos";
        }
    }

    @PostMapping("/movimientos/movtipos/eliminar/{id}")
    public String eliminarMovtipo(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            movtipoService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tipo de movimiento eliminado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/config/movimientos/movtipos";
    }

    private Long resolveRoleId(Long roleId) {
        if (roleId != null) {
            return roleId;
        }
        return roleService.findAllRoles().stream()
                .findFirst()
                .map(role -> role.getId())
                .orElse(null);
    }
}
