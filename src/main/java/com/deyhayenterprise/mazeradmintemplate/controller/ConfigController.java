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
import com.deyhayenterprise.mazeradmintemplate.service.RoleService;
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
        if (!model.containsAttribute("permissionForm")) {
            RolePermissionUpdateForm form = new RolePermissionUpdateForm();
            form.setOptionIds(roleService.findAssignedOptionIds(selectedRoleId));
            model.addAttribute("permissionForm", form);
        }
        return "config/permisos";
    }

    @PostMapping("/permisos/{roleId}")
    public String actualizarPermisos(@PathVariable Long roleId,
                                     @ModelAttribute("permissionForm") RolePermissionUpdateForm permissionForm,
                                     RedirectAttributes redirectAttributes) {
        roleService.updateRolePermissions(roleId, permissionForm.getOptionIds());
        redirectAttributes.addFlashAttribute("successMessage", "Permisos actualizados correctamente.");
        return "redirect:/config/permisos?roleId=" + roleId;
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
