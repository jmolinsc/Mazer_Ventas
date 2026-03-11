package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.entity.UnidadMedida;
import com.deyhayenterprise.mazeradmintemplate.web.form.UnidadMedidaForm;

public interface UnidadMedidaService {

    List<UnidadMedida> findAllActive();

    List<String> findAllNames();

    UnidadMedida findActiveById(Long id);

    UnidadMedida create(UnidadMedidaForm form);

    UnidadMedida update(Long id, UnidadMedidaForm form);

    void delete(Long id);
}
