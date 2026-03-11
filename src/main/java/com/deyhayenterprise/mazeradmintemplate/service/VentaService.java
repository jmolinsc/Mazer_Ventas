package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.web.form.VentaCreateForm;

public interface VentaService {

    List<Venta> findAll();

    Venta findById(Long id);

    Venta create(VentaCreateForm form,String accion);

    Venta update(Long id, VentaCreateForm form, String accion);
}
