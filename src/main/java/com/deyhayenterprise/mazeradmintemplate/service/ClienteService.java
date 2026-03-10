package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.Cliente;
import com.deyhayenterprise.mazeradmintemplate.repository.ClienteRepository;
import com.deyhayenterprise.mazeradmintemplate.web.form.ClienteCreateForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return clienteRepository.findAllByActivoTrueOrderByIdDesc();
    }

    @Transactional(readOnly = true)
    public Cliente findActiveById(Long id) {
        return clienteRepository.findByIdAndActivoTrue(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
    }

    @Transactional
    public Cliente create(ClienteCreateForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (clienteRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ya existe un cliente con ese correo.");
        }
        Cliente cliente = new Cliente();
        applyValues(cliente, form, email);
        cliente.setActivo(true);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente update(Long id, ClienteCreateForm form) {
        Cliente cliente = findActiveById(id);
        String email = form.getEmail().trim().toLowerCase();
        if (clienteRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new IllegalArgumentException("Ya existe un cliente con ese correo.");
        }
        applyValues(cliente, form, email);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void delete(Long id) {
        Cliente cliente = findActiveById(id);
        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    private void applyValues(Cliente cliente, ClienteCreateForm form, String normalizedEmail) {
        cliente.setNombre(form.getNombre().trim());
        cliente.setEmail(normalizedEmail);
        cliente.setTelefono(form.getTelefono() == null ? null : form.getTelefono().trim());
        cliente.setCategoria(form.getCategoria().trim());
        cliente.setDireccion(form.getDireccion() == null ? null : form.getDireccion().trim());
    }
}
