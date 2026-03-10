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
        return productoRepository.findAllByActivoTrueOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public Producto findActiveById(Long id) {
        return productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));
    }

    @Transactional
    public Producto create(ProductoCreateForm form) {
        String codigo = normalizeCode(form.getCodigo());
        if (productoRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new IllegalArgumentException("Ya existe un producto con ese código.");
        }
        Producto producto = new Producto();
        applyValues(producto, form, codigo);
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    @Transactional
    public Producto update(Long id, ProductoCreateForm form) {
        Producto producto = findActiveById(id);
        String codigo = normalizeCode(form.getCodigo());
        if (productoRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            throw new IllegalArgumentException("Ya existe un producto con ese código.");
        }
        applyValues(producto, form, codigo);
        return productoRepository.save(producto);
    }

    @Transactional
    public void delete(Long id) {
        Producto producto = findActiveById(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private String normalizeCode(String rawCode) {
        return rawCode.trim().toUpperCase(Locale.ROOT);
    }

    private void applyValues(Producto producto, ProductoCreateForm form, String normalizedCode) {
        producto.setCodigo(normalizedCode);
        producto.setNombre(form.getNombre().trim());
        producto.setCategoria(form.getCategoria().trim());
        producto.setPrecio(form.getPrecio());
        producto.setStock(form.getStock());
        producto.setUnidad(form.getUnidad().trim());
    }
}
