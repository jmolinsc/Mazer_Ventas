package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.Comportamiento;
import com.deyhayenterprise.mazeradmintemplate.entity.Movtipo;
import com.deyhayenterprise.mazeradmintemplate.repository.ComportamientoRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.MovtipoRepository;
import com.deyhayenterprise.mazeradmintemplate.service.MovtipoService;
import com.deyhayenterprise.mazeradmintemplate.web.form.MovtipoForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovtipoServiceImpl implements MovtipoService {

    private final MovtipoRepository movtipoRepository;
    private final ComportamientoRepository comportamientoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Movtipo> findAllActive() {
        return movtipoRepository.findAllByActivoTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public Movtipo findActiveById(Long id) {
        return movtipoRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Movtipo no encontrado."));
    }

    @Override
    @Transactional
    public Movtipo create(MovtipoForm form) {
        Comportamiento comportamiento = comportamientoRepository.findByIdAndActivoTrue(form.getComportamientoId())
                .orElseThrow(() -> new IllegalArgumentException("Comportamiento no encontrado."));

        String nombre = normalize(form.getNombre());
        if (movtipoRepository.existsByNombreIgnoreCaseAndComportamientoIdAndActivoTrue(nombre, comportamiento.getId())) {
            throw new IllegalArgumentException("Ya existe ese tipo de movimiento para el comportamiento seleccionado.");
        }

        Movtipo movtipo = new Movtipo();
        movtipo.setNombre(nombre);
        movtipo.setComportamiento(comportamiento);
        movtipo.setActivo(true);
        return movtipoRepository.save(movtipo);
    }

    @Override
    @Transactional
    public Movtipo update(Long id, MovtipoForm form) {
        Movtipo movtipo = findActiveById(id);
        Comportamiento comportamiento = comportamientoRepository.findByIdAndActivoTrue(form.getComportamientoId())
                .orElseThrow(() -> new IllegalArgumentException("Comportamiento no encontrado."));

        String nombre = normalize(form.getNombre());
        if (movtipoRepository.existsByNombreIgnoreCaseAndComportamientoIdAndIdNot(nombre, comportamiento.getId(), id)) {
            throw new IllegalArgumentException("Ya existe ese tipo de movimiento para el comportamiento seleccionado.");
        }

        movtipo.setNombre(nombre);
        movtipo.setComportamiento(comportamiento);
        return movtipoRepository.save(movtipo);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Movtipo movtipo = findActiveById(id);
        movtipo.setActivo(false);
        movtipoRepository.save(movtipo);
    }

    private String normalize(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}

