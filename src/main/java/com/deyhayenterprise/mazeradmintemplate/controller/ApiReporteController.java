package com.deyhayenterprise.mazeradmintemplate.controller;


import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.service.reporte.ReportesNegocioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reportes")
public class ApiReporteController {

    @Autowired
    private ReportesNegocioService reportesNegocioService;

    @GetMapping("/ventas")
    public ResponseEntity<byte[]> descargarVentas() {
        // Ejemplo: Aquí normalmente llamarías a tu base de datos mediante un servicio/repositorio
        List<Venta> datosSimulados = new ArrayList<>();

        byte[] pdfBytes = reportesNegocioService.generarReporteVentas(datosSimulados, "Carlos Deyhay");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("ventas.pdf").build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
