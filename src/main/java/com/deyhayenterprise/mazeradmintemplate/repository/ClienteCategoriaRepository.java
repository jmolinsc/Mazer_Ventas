package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.ClienteCategoria;

public interface ClienteCategoriaRepository extends JpaRepository<ClienteCategoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<ClienteCategoria> findAllByActivoTrueOrderByNombreAsc();

    Optional<ClienteCategoria> findByIdAndActivoTrue(Long id);
}

