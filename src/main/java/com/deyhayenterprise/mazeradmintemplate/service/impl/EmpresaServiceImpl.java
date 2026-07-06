package com.deyhayenterprise.mazeradmintemplate.service.impl;

import com.deyhayenterprise.mazeradmintemplate.entity.Empresa;
import com.deyhayenterprise.mazeradmintemplate.repository.EmpresaRepository;
import com.deyhayenterprise.mazeradmintemplate.service.EmpresaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class EmpresaServiceImpl implements EmpresaService {


  private final  EmpresaRepository empresaRepository;

    @Override
    public Empresa findFirstBy() {
        return empresaRepository.findFirstBy();
    }

    @Override
    public Empresa save(Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    @Override
    public Empresa update(Long id, Empresa empresa) {
        return empresaRepository.save(empresa);
    }

    @Override
    public void cargarEmpresa() {

    }
}
