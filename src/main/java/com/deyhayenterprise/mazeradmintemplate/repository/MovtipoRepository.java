package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.Movtipo;

public interface MovtipoRepository extends JpaRepository<Movtipo, Long> {

    @EntityGraph(attributePaths = {"comportamiento", "comportamiento.modulo"})
    List<Movtipo> findAllByActivoTrueOrderByNombreAsc();

    @EntityGraph(attributePaths = {"comportamiento", "comportamiento.modulo"})
    Optional<Movtipo> findByIdAndActivoTrue(Long id);

    boolean existsByNombreIgnoreCaseAndComportamientoIdAndActivoTrue(String nombre, Long comportamientoId);

    boolean existsByNombreIgnoreCaseAndComportamientoIdAndIdNot(String nombre, Long comportamientoId, Long id);
}

