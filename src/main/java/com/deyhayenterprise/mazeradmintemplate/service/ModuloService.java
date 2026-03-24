package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.entity.Modulo;
import com.deyhayenterprise.mazeradmintemplate.web.form.ModuloForm;

public interface ModuloService {

    List<Modulo> findAllActive();

    Modulo findActiveById(Long id);

    Modulo create(ModuloForm form);

    Modulo update(Long id, ModuloForm form);

    void delete(Long id);
}

