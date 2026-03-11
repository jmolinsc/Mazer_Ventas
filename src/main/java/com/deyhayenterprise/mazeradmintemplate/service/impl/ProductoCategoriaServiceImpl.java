package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.ProductoCategoria;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoCategoriaRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoRepository;
import com.deyhayenterprise.mazeradmintemplate.service.ProductoCategoriaService;
import com.deyhayenterprise.mazeradmintemplate.web.form.ProductoCategoriaForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoCategoriaServiceImpl implements ProductoCategoriaService {

    private final ProductoCategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductoCategoria> findAllActive() {
        return categoriaRepository.findAllByActivoTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllCategoryNames() {
        return findAllActive().stream().map(ProductoCategoria::getNombre).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoCategoria findActiveById(Long id) {
        return categoriaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria de producto no encontrada."));
    }

    @Override
    @Transactional
    public ProductoCategoria create(ProductoCategoriaForm form) {
        String nombre = normalizeName(form.getNombre());
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalArgumentException("Ya existe una categoria con ese nombre.");
        }
        ProductoCategoria c = new ProductoCategoria();
        c.setNombre(nombre);
        c.setDescripcion(normalizeDescription(form.getDescripcion()));
        c.setActivo(true);
        return categoriaRepository.save(c);
    }

    @Override
    @Transactional
    public ProductoCategoria update(Long id, ProductoCategoriaForm form) {
        ProductoCategoria c = findActiveById(id);
        String nombre = normalizeName(form.getNombre());
        if (categoriaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new IllegalArgumentException("Ya existe una categoria con ese nombre.");
        }
        c.setNombre(nombre);
        c.setDescripcion(normalizeDescription(form.getDescripcion()));
        return categoriaRepository.save(c);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProductoCategoria c = findActiveById(id);
        if (productoRepository.findAllByActivoTrueOrderByIdDesc().stream()
                .anyMatch(producto -> producto.getCategoria() != null && producto.getCategoria().equalsIgnoreCase(c.getNombre()))) {
            throw new IllegalArgumentException("No se puede eliminar la categoria porque tiene productos asociados.");
        }
        c.setActivo(false);
        categoriaRepository.save(c);
    }

    private String normalizeName(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeDescription(String value) {
        if (value == null) return null;
        String v = value.trim();
        return v.isEmpty() ? null : v;
    }
}

