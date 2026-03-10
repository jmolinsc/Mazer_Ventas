package com.deyhayenterprise.mazeradmintemplate.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/cuentas-cobrar")
@RequiredArgsConstructor
@Slf4j
public class CuentasCobrarController {

    @GetMapping("/pendientes")
    public String pendientes(Model model) {
        log.info("Cuentas pendientes");
        model.addAttribute("pageHeading", "Cuentas Pendientes");
        model.addAttribute("pageSubtitle", "Cobros pendientes por clientes");
        return "cuentas-cobrar/pendientes";
    }

    @GetMapping("/pagos")
    public String registrarPago(Model model) {
        log.info("Registrar pago");
        model.addAttribute("pageHeading", "Registrar Pago");
        model.addAttribute("pageSubtitle", "Procesar pagos de clientes");
        return "cuentas-cobrar/pagos";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        log.info("Reportes de cuentas por cobrar");
        model.addAttribute("pageHeading", "Reportes de Cuentas por Cobrar");
        model.addAttribute("pageSubtitle", "Análisis de cartera");
        return "cuentas-cobrar/reportes";
    }
}

