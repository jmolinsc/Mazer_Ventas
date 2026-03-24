package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.Comportamiento;

public interface ComportamientoRepository extends JpaRepository<Comportamiento, Long> {

    @EntityGraph(attributePaths = "modulo")
    List<Comportamiento> findAllByActivoTrueOrderByNombreAsc();

    @EntityGraph(attributePaths = "modulo")
    Optional<Comportamiento> findByIdAndActivoTrue(Long id);

    boolean existsByNombreIgnoreCaseAndModuloIdAndActivoTrue(String nombre, Long moduloId);

    boolean existsByNombreIgnoreCaseAndModuloIdAndIdNot(String nombre, Long moduloId, Long id);
}

