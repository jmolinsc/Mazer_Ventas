package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.web.form.ProductoCreateForm;

public interface ProductoService {

    List<Producto> findAll();

    Producto findActiveById(Long id);

    Producto create(ProductoCreateForm form);

    Producto update(Long id, ProductoCreateForm form);

    void delete(Long id);
}
