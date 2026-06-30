package com.deyhayenterprise.mazeradmintemplate.repository;

import com.deyhayenterprise.mazeradmintemplate.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Empresa findFirstBy();
}
