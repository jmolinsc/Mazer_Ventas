package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @EntityGraph(attributePaths = {"cliente", "detalles", "detalles.producto", "producto"})
    List<Venta> findAllByOrderByFechaDescIdDesc();

    @Override
    @EntityGraph(attributePaths = {"cliente", "detalles", "detalles.producto", "producto"})
    Optional<Venta> findById(Long id);
}
