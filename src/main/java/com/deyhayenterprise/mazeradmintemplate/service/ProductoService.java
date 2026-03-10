package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoRepository;
import com.deyhayenterprise.mazeradmintemplate.web.form.ProductoCreateForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Transactional
    public Producto create(ProductoCreateForm form) {
        String codigo = form.getCodigo().trim().toUpperCase(Locale.ROOT);
        if (productoRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new IllegalArgumentException("Ya existe un producto con ese código.");
        }

        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre(form.getNombre().trim());
        producto.setCategoria(form.getCategoria().trim());
        producto.setPrecio(form.getPrecio());
        producto.setStock(form.getStock());
        producto.setUnidad(form.getUnidad().trim());
        producto.setActivo(true);

        return productoRepository.save(producto);
    }
}

