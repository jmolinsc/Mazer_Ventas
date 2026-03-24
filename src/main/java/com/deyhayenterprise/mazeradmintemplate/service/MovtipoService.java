package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.entity.Movtipo;
import com.deyhayenterprise.mazeradmintemplate.web.form.MovtipoForm;

public interface MovtipoService {

    List<Movtipo> findAllActive();

    Movtipo findActiveById(Long id);

    Movtipo create(MovtipoForm form);

    Movtipo update(Long id, MovtipoForm form);

    void delete(Long id);
}

