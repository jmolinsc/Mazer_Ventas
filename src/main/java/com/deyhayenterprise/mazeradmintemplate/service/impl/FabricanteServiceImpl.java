package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.Fabricante;
import com.deyhayenterprise.mazeradmintemplate.repository.FabricanteRepository;
import com.deyhayenterprise.mazeradmintemplate.service.FabricanteService;
import com.deyhayenterprise.mazeradmintemplate.web.form.FabricanteForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FabricanteServiceImpl implements FabricanteService {

    private final FabricanteRepository fabricanteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Fabricante> findAll() {
        return fabricanteRepository.findAllByActivoTrueOrderByIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Fabricante findActiveById(Long id) {
        return fabricanteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Fabricante no encontrado."));
    }

    @Override
    @Transactional
    public Fabricante create(FabricanteForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (fabricanteRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ya existe un fabricante con ese correo.");
        }
        Fabricante f = new Fabricante();
        applyValues(f, form, email);
        f.setActivo(true);
        return fabricanteRepository.save(f);
    }

    @Override
    @Transactional
    public Fabricante update(Long id, FabricanteForm form) {
        Fabricante f = findActiveById(id);
        String email = form.getEmail().trim().toLowerCase();
        if (fabricanteRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new IllegalArgumentException("Ya existe un fabricante con ese correo.");
        }
        applyValues(f, form, email);
        return fabricanteRepository.save(f);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Fabricante f = findActiveById(id);
        f.setActivo(false);
        fabricanteRepository.save(f);
    }

    private void applyValues(Fabricante f, FabricanteForm form, String normalizedEmail) {
        f.setNombre(form.getNombre().trim());
        f.setEmail(normalizedEmail);
        f.setTelefono(form.getTelefono() == null ? null : form.getTelefono().trim());
        f.setPais(form.getPais().trim());
        f.setDireccion(form.getDireccion() == null ? null : form.getDireccion().trim());
    }
}

