package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.entity.ClienteCategoria;
import com.deyhayenterprise.mazeradmintemplate.web.form.ClienteCategoriaForm;

public interface ClienteCategoriaService {

    List<ClienteCategoria> findAllActive();

    List<String> findAllCategoryNames();

    ClienteCategoria findActiveById(Long id);

    ClienteCategoria create(ClienteCategoriaForm form);

    ClienteCategoria update(Long id, ClienteCategoriaForm form);

    void delete(Long id);
}
