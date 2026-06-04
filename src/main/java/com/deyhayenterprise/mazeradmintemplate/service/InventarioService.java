package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.entity.Inventario;
import com.deyhayenterprise.mazeradmintemplate.web.form.InventarioCreateForm;

public interface InventarioService {

    List<Inventario> findAll();

    Inventario findById(Long id);

    Inventario executeTransaction(Long id, InventarioCreateForm form, String accion, String comportamiento);
}

