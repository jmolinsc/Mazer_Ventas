package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    @Value("${app.upload.productos-dir:uploads/productos}")
    private String uploadDir;

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
    public Producto create(ProductoCreateForm form, MultipartFile imagenFile) {
        String codigo = normalizeCode(form.getCodigo());
        if (productoRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new IllegalArgumentException("Ya existe un producto con ese codigo.");
        }
        Producto producto = new Producto();
        applyValues(producto, form, codigo, imagenFile);
        producto.setActivo(true);
        return productoRepository.save(producto);
    }

    @Override
    @Transactional
    public Producto update(Long id, ProductoCreateForm form, MultipartFile imagenFile) {
        Producto producto = findActiveById(id);
        String codigo = normalizeCode(form.getCodigo());
        if (productoRepository.existsByCodigoIgnoreCaseAndIdNot(codigo, id)) {
            throw new IllegalArgumentException("Ya existe un producto con ese codigo.");
        }
        applyValues(producto, form, codigo, imagenFile);
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

    private void applyValues(Producto producto, ProductoCreateForm form, String normalizedCode, MultipartFile imagenFile) {
        Fabricante fabricante = fabricanteRepository.findByIdAndActivoTrue(form.getFabricanteId())
                .orElseThrow(() -> new IllegalArgumentException("Fabricante no encontrado."));

        String unidad = form.getUnidad() == null ? "" : form.getUnidad().trim();
        boolean unidadValida = unidadMedidaRepository.existsByNombreIgnoreCaseAndActivoTrue(unidad);
        if (!unidadValida) {
            throw new IllegalArgumentException("La unidad seleccionada no es valida.");
        }

        Integer stockMinimo = sanitizeOptionalInteger(form.getStockMinimo());
        Integer stockMaximo = sanitizeOptionalInteger(form.getStockMaximo());
        if (stockMinimo != null && stockMaximo != null && stockMinimo > stockMaximo) {
            throw new IllegalArgumentException("El stock minimo no puede ser mayor al stock maximo.");
        }

        producto.setCodigo(normalizedCode);
        producto.setNombre(form.getNombre().trim());
        producto.setCategoria(form.getCategoria().trim());
        producto.setPrecio(form.getPrecio());
        producto.setCosto(sanitizeOptionalAmount(form.getCosto()));
        producto.setStock(form.getStock());
        producto.setStockMinimo(stockMinimo);
        producto.setStockMaximo(stockMaximo);
        producto.setUnidad(unidad);
        producto.setUbicacion(trimToNull(form.getUbicacion()));
        producto.setDescripcion(trimToNull(form.getDescripcion()));
        producto.setFabricante(fabricante);

        String imagenActual = trimToNull(form.getImagenUrl());
        producto.setImagenUrl(imagenActual);
        if (imagenFile != null && !imagenFile.isEmpty()) {
            producto.setImagenUrl(storeImage(imagenFile));
        }
    }

    private BigDecimal sanitizeOptionalAmount(BigDecimal value) {
        return value == null ? null : value;
    }

    private Integer sanitizeOptionalInteger(Integer value) {
        return value == null ? null : value;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String storeImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("Solo se permiten archivos de imagen.");
        }

        String originalName = file.getOriginalFilename() == null ? "imagen" : file.getOriginalFilename();
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex).toLowerCase(Locale.ROOT);
        }

        try {
            Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String filename = UUID.randomUUID() + extension;
            Path targetFile = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/productos/" + filename;
        } catch (IOException ex) {
            throw new IllegalArgumentException("No se pudo guardar la imagen del producto.");
        }
    }
}
