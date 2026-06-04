package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.Inventario;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "movtipo", "movtipo.comportamiento"})
    List<Inventario> findAllByOrderByFechaDescIdDesc();

    @Override
    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "movtipo", "movtipo.comportamiento"})
    Optional<Inventario> findById(Long id);

    long countByMovIgnoreCase(String mov);
}

