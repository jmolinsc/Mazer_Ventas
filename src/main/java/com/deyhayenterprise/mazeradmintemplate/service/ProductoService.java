package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.web.form.ProductoCreateForm;

public interface ProductoService {

    List<Producto> findAll();

    Producto findActiveById(Long id);

    Producto create(ProductoCreateForm form, MultipartFile imagenFile);

    Producto update(Long id, ProductoCreateForm form, MultipartFile imagenFile);

    void delete(Long id);
}
