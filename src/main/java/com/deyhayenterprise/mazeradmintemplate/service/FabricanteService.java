package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.entity.Fabricante;
import com.deyhayenterprise.mazeradmintemplate.web.form.FabricanteForm;

public interface FabricanteService {

    List<Fabricante> findAll();

    Fabricante findActiveById(Long id);

    Fabricante create(FabricanteForm form);

    Fabricante update(Long id, FabricanteForm form);

    void delete(Long id);
}
