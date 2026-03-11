package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.ClienteCategoria;
import com.deyhayenterprise.mazeradmintemplate.repository.ClienteCategoriaRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ClienteRepository;
import com.deyhayenterprise.mazeradmintemplate.service.ClienteCategoriaService;
import com.deyhayenterprise.mazeradmintemplate.web.form.ClienteCategoriaForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteCategoriaServiceImpl implements ClienteCategoriaService {

    private final ClienteCategoriaRepository categoriaRepository;
    private final ClienteRepository clienteRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteCategoria> findAllActive() {
        return categoriaRepository.findAllByActivoTrueOrderByNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findAllCategoryNames() {
        return findAllActive().stream().map(ClienteCategoria::getNombre).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteCategoria findActiveById(Long id) {
        return categoriaRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoria de cliente no encontrada."));
    }

    @Override
    @Transactional
    public ClienteCategoria create(ClienteCategoriaForm form) {
        String nombre = normalizeName(form.getNombre());
        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new IllegalArgumentException("Ya existe una categoria con ese nombre.");
        }
        ClienteCategoria c = new ClienteCategoria();
        c.setNombre(nombre);
        c.setDescripcion(normalizeDescription(form.getDescripcion()));
        c.setActivo(true);
        return categoriaRepository.save(c);
    }

    @Override
    @Transactional
    public ClienteCategoria update(Long id, ClienteCategoriaForm form) {
        ClienteCategoria c = findActiveById(id);
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
        ClienteCategoria c = findActiveById(id);
        if (clienteRepository.findAllByActivoTrueOrderByIdDesc().stream()
                .anyMatch(cliente -> cliente.getCategoria() != null && cliente.getCategoria().equalsIgnoreCase(c.getNombre()))) {
            throw new IllegalArgumentException("No se puede eliminar la categoria porque tiene clientes asociados.");
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

