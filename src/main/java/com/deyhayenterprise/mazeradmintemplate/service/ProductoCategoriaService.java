package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.entity.ProductoCategoria;
import com.deyhayenterprise.mazeradmintemplate.web.form.ProductoCategoriaForm;

public interface ProductoCategoriaService {

    List<ProductoCategoria> findAllActive();

    List<String> findAllCategoryNames();

    ProductoCategoria findActiveById(Long id);

    ProductoCategoria create(ProductoCategoriaForm form);

    ProductoCategoria update(Long id, ProductoCategoriaForm form);

    void delete(Long id);
}
