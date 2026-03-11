package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.UnidadMedida;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.UnidadMedidaRepository;
import com.deyhayenterprise.mazeradmintemplate.service.UnidadMedidaService;
import com.deyhayenterprise.mazeradmintemplate.web.form.UnidadMedidaForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnidadMedidaServiceImpl implements UnidadMedidaService {

    private final UnidadMedidaRepository unidadMedidaRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UnidadMedida> findAllActive() {
        return unidadMedidaRepository.findAllByActivoTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllNames() {
        return findAllActive().stream().map(UnidadMedida::getNombre).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadMedida findActiveById(Long id) {
        return unidadMedidaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Unidad no encontrada."));
    }

    @Override
    @Transactional
    public UnidadMedida create(UnidadMedidaForm form) {
        String nombre = normalize(form.getNombre());
        if (unidadMedidaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalArgumentException("Ya existe una unidad con ese nombre.");
        }
        UnidadMedida unidad = new UnidadMedida();
        unidad.setNombre(nombre);
        unidad.setDescripcion(normalizeDescription(form.getDescripcion()));
        unidad.setActivo(true);
        return unidadMedidaRepository.save(unidad);
    }

    @Override
    @Transactional
    public UnidadMedida update(Long id, UnidadMedidaForm form) {
        UnidadMedida unidad = findActiveById(id);
        String nombre = normalize(form.getNombre());
        if (unidadMedidaRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new IllegalArgumentException("Ya existe una unidad con ese nombre.");
        }
        unidad.setNombre(nombre);
        unidad.setDescripcion(normalizeDescription(form.getDescripcion()));
        return unidadMedidaRepository.save(unidad);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        UnidadMedida unidad = findActiveById(id);
        boolean enUso = productoRepository.findAllByActivoTrueOrderByIdDesc().stream()
                .anyMatch(p -> p.getUnidad() != null && p.getUnidad().equalsIgnoreCase(unidad.getNombre()));
        if (enUso) {
            throw new IllegalArgumentException("No se puede eliminar la unidad porque tiene productos asociados.");
        }
        unidad.setActivo(false);
        unidadMedidaRepository.save(unidad);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeDescription(String value) {
        if (value == null) {
            return null;
        }
        String result = value.trim();
        return result.isEmpty() ? null : result;
    }
}

