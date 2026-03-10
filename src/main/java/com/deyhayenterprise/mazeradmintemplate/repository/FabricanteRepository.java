package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.Fabricante;

public interface FabricanteRepository extends JpaRepository<Fabricante, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Fabricante> findAllByActivoTrueOrderByIdDesc();

    Optional<Fabricante> findByIdAndActivoTrue(Long id);

    Optional<Fabricante> findByEmailIgnoreCase(String email);
}

