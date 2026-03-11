package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.Fabricante;
import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.repository.FabricanteRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.UnidadMedidaRepository;
import com.deyhayenterprise.mazeradmintemplate.service.ProductoService;
import com.deyhayenterprise.mazeradmintemplate.web.form.ProductoCreateForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final FabricanteRepository fabricanteRepository;
    private final UnidadMedidaRepository unidadMedidaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepository.findAllByActivoTrueOrderByIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto findActiveById(Long id) {
        return productoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));
    }

    @Override
    @Transactional
    public Producto create(ProductoCreateForm form) {
        String codigo = normalizeCode(form.getCodigo());
        if (productoRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new IllegalArgumentException("Ya existe un producto con ese codigo.");
        }
        Producto producto = new Producto();
        applyValues(producto, form, codigo);
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public Producto update(Long id, ProductoCreateForm form) {
        Producto producto = findActiveById(id);
        String codigo = normalizeCode(form.getCodigo());
        if (productoRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            throw new IllegalArgumentException("Ya existe un producto con ese codigo.");
        }
        applyValues(producto, form, codigo);
        return productoRepository.save(producto);
    }

    @Override
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
        Fabricante fabricante = fabricanteRepository.findByIdAndActivoTrue(form.getFabricanteId())
                .orElseThrow(() -> new IllegalArgumentException("Fabricante no encontrado."));

        String unidad = form.getUnidad() == null ? "" : form.getUnidad().trim();
        boolean unidadValida = unidadMedidaRepository.existsByNombreIgnoreCaseAndActivoTrue(unidad);
        if (!unidadValida) {
            throw new IllegalArgumentException("La unidad seleccionada no es valida.");
        }

        producto.setCodigo(normalizedCode);
        producto.setNombre(form.getNombre().trim());
        producto.setCategoria(form.getCategoria().trim());
        producto.setPrecio(form.getPrecio());
        producto.setStock(form.getStock());
        producto.setUnidad(unidad);
        producto.setFabricante(fabricante);
    }
}

