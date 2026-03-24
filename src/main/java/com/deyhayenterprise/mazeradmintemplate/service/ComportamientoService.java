package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.entity.Comportamiento;
import com.deyhayenterprise.mazeradmintemplate.web.form.ComportamientoForm;

public interface ComportamientoService {

    List<Comportamiento> findAllActive();

    Comportamiento findActiveById(Long id);

    Comportamiento create(ComportamientoForm form);

    Comportamiento update(Long id, ComportamientoForm form);

    void delete(Long id);
}

