package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.entity.Cliente;
import com.deyhayenterprise.mazeradmintemplate.web.form.ClienteCreateForm;

public interface ClienteService {

    List<Cliente> findAll();

    Cliente findActiveById(Long id);

    Cliente create(ClienteCreateForm form);

    Cliente update(Long id, ClienteCreateForm form);

    void delete(Long id);
}
