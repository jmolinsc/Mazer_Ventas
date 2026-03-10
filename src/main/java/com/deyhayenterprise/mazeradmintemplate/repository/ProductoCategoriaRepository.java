package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.ProductoCategoria;

public interface ProductoCategoriaRepository extends JpaRepository<ProductoCategoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    List<ProductoCategoria> findAllByActivoTrueOrderByNombreAsc();

    Optional<ProductoCategoria> findByIdAndActivoTrue(Long id);
}

