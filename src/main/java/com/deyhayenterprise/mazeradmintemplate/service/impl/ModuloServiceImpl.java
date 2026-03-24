package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.Modulo;
import com.deyhayenterprise.mazeradmintemplate.repository.ComportamientoRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ModuloRepository;
import com.deyhayenterprise.mazeradmintemplate.service.ModuloService;
import com.deyhayenterprise.mazeradmintemplate.web.form.ModuloForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModuloServiceImpl implements ModuloService {

    private final ModuloRepository moduloRepository;
    private final ComportamientoRepository comportamientoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Modulo> findAllActive() {
        return moduloRepository.findAllByActivoTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Modulo findActiveById(Long id) {
        return moduloRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Módulo no encontrado."));
    }

    @Override
    @Transactional
    public Modulo create(ModuloForm form) {
        String nombre = normalize(form.getNombre());
        if (moduloRepository.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalArgumentException("Ya existe un módulo con ese nombre.");
        }
        Modulo modulo = new Modulo();
        modulo.setNombre(nombre);
        modulo.setActivo(true);
        return moduloRepository.save(modulo);
    }

    @Override
    @Transactional
    public Modulo update(Long id, ModuloForm form) {
        Modulo modulo = findActiveById(id);
        String nombre = normalize(form.getNombre());
        if (moduloRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new IllegalArgumentException("Ya existe un módulo con ese nombre.");
        }
        modulo.setNombre(nombre);
        return moduloRepository.save(modulo);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Modulo modulo = findActiveById(id);
        boolean inUse = comportamientoRepository.findAllByActivoTrueOrderByNombreAsc().stream()
                .anyMatch(c -> c.getModulo().getId().equals(id));
        if (inUse) {
            throw new IllegalArgumentException("No se puede eliminar el módulo porque tiene comportamientos asociados.");
        }
        modulo.setActivo(false);
        moduloRepository.save(modulo);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}

