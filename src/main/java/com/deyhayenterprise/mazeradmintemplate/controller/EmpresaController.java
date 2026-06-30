package com.deyhayenterprise.mazeradmintemplate.controller;

import com.deyhayenterprise.mazeradmintemplate.entity.Empresa;
import com.deyhayenterprise.mazeradmintemplate.service.EmpresaService;
import com.deyhayenterprise.mazeradmintemplate.service.impl.EmpresaServiceImpl;
import com.deyhayenterprise.mazeradmintemplate.web.form.EmpresaForm;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/empresas")
@RequiredArgsConstructor
@Slf4j
public class EmpresaController {

    private final EmpresaServiceImpl empresaService;
    // Método que recibe el ID como query parameter
    @PostMapping("/save")
    public String saveEmpresa(
            @RequestParam(value = "id", required = false) Long id,
            @Valid @ModelAttribute("empresaForm") EmpresaForm form,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        log.info("=== GUARDANDO EMPRESA DESDE EMPRESACONTROLLER ===");
        log.info("ID: {}", id);

        if (result.hasErrors()) {
            log.warn("Errores de validación: {}", result.getAllErrors());
            // Redirige de vuelta con errores
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.empresaForm", result);
            redirectAttributes.addFlashAttribute("empresaForm", form);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar: " + result.getAllErrors());
            return "redirect:/config/empresa";
        }

        try {
            if (id != null && id > 0) {
                // Actualizar existente
                empresaService.update(id, FormToEmpresa(id, form));
                redirectAttributes.addFlashAttribute("successMessage", "Empresa actualizada correctamente.");
            } else {
                // Crear nuevo
                empresaService.save(FormToEmpresa(null, form));
                redirectAttributes.addFlashAttribute("successMessage", "Empresa creada correctamente.");
            }
            return "redirect:/config/empresa";

        } catch (Exception e) {
            log.error("Error al guardar empresa", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar: " + e.getMessage());
            return "redirect:/config/empresa";
        }
    }



    public Empresa FormToEmpresa(Long id, EmpresaForm form) {
        Empresa empresa = new Empresa();
        empresa.setId(id);
        empresa.setNombre(form.getNombre());
        empresa.setRazonsocial(form.getRazonsocial());
        empresa.setNit(form.getNit());
        empresa.setNrc(form.getNrc());
        empresa.setEmail(form.getEmail());
        empresa.setTelefono(form.getTelefono());
        empresa.setDireccion(form.getDireccion());
        return empresa;
    }
}
