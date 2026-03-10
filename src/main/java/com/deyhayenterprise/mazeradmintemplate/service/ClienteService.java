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
        return clienteRepository.findAll();
    }

    @Transactional
    public Cliente create(ClienteCreateForm form) {
        String email = form.getEmail().trim().toLowerCase();
        if (clienteRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ya existe un cliente con ese correo.");
        }

        Cliente cliente = new Cliente();
        cliente.setNombre(form.getNombre().trim());
        cliente.setEmail(email);
        cliente.setTelefono(form.getTelefono() == null ? null : form.getTelefono().trim());
        cliente.setCategoria(form.getCategoria().trim());
        cliente.setDireccion(form.getDireccion() == null ? null : form.getDireccion().trim());
        cliente.setActivo(true);

        return clienteRepository.save(cliente);
    }
}

