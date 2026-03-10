package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.Venta;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    @EntityGraph(attributePaths = {"cliente", "producto"})
    List<Venta> findAllByOrderByFechaDescIdDesc();
}

