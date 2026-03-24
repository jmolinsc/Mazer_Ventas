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
import com.deyhayenterprise.mazeradmintemplate.service.MenuAdminService;
import com.deyhayenterprise.mazeradmintemplate.service.RoleService;
import com.deyhayenterprise.mazeradmintemplate.web.form.AppMenuForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.MenuOptionForm;
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
