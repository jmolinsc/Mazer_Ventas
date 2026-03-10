package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigoIgnoreCase(String codigo);

    Optional<Producto> findByCodigoIgnoreCase(String codigo);
}
