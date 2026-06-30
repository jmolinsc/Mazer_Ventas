package com.deyhayenterprise.mazeradmintemplate.service;

import com.deyhayenterprise.mazeradmintemplate.entity.Empresa;

public interface EmpresaService {

    Empresa findFirstBy();

    Empresa save(Empresa empresa);
    Empresa update(Long id, Empresa empresa);
}
