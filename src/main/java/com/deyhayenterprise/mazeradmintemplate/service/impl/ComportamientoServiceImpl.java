package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.Comportamiento;
import com.deyhayenterprise.mazeradmintemplate.entity.Modulo;
import com.deyhayenterprise.mazeradmintemplate.repository.ComportamientoRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ModuloRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.MovtipoRepository;
import com.deyhayenterprise.mazeradmintemplate.service.ComportamientoService;
import com.deyhayenterprise.mazeradmintemplate.web.form.ComportamientoForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComportamientoServiceImpl implements ComportamientoService {

    private final ComportamientoRepository comportamientoRepository;
    private final ModuloRepository moduloRepository;
    private final MovtipoRepository movtipoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Comportamiento> findAllActive() {
        return comportamientoRepository.findAllByActivoTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Comportamiento findActiveById(Long id) {
        return comportamientoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Comportamiento no encontrado."));
    }

    @Override
    @Transactional
    public Comportamiento create(ComportamientoForm form) {
        Modulo modulo = moduloRepository.findByIdAndActivoTrue(form.getModuloId())
                .orElseThrow(() -> new IllegalArgumentException("Módulo no encontrado."));

        String nombre = normalize(form.getNombre());
        if (comportamientoRepository.existsByNombreIgnoreCaseAndModuloIdAndActivoTrue(nombre, modulo.getId())) {
            throw new IllegalArgumentException("Ya existe ese comportamiento para el módulo seleccionado.");
        }

        Comportamiento c = new Comportamiento();
        c.setNombre(nombre);
        c.setModulo(modulo);
        c.setActivo(true);
        return comportamientoRepository.save(c);
    }

    @Override
    @Transactional
    public Comportamiento update(Long id, ComportamientoForm form) {
        Comportamiento c = findActiveById(id);
        Modulo modulo = moduloRepository.findByIdAndActivoTrue(form.getModuloId())
                .orElseThrow(() -> new IllegalArgumentException("Módulo no encontrado."));

        String nombre = normalize(form.getNombre());
        if (comportamientoRepository.existsByNombreIgnoreCaseAndModuloIdAndIdNot(nombre, modulo.getId(), id)) {
            throw new IllegalArgumentException("Ya existe ese comportamiento para el módulo seleccionado.");
        }

        c.setNombre(nombre);
        c.setModulo(modulo);
        return comportamientoRepository.save(c);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Comportamiento c = findActiveById(id);
        boolean inUse = movtipoRepository.findAllByActivoTrueOrderByNombreAsc().stream()
                .anyMatch(m -> m.getComportamiento().getId().equals(id));
        if (inUse) {
            throw new IllegalArgumentException("No se puede eliminar el comportamiento porque tiene movimientos asociados.");
        }
        c.setActivo(false);
        comportamientoRepository.save(c);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}

