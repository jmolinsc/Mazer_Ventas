package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    @EntityGraph(attributePaths = "fabricante")
    List<Producto> findAllByActivoTrueOrderByIdDesc();

    Optional<Producto> findByIdAndActivoTrue(Long id);

    Optional<Producto> findByCodigoIgnoreCase(String codigo);

    List<Producto> findAllByFabricanteIsNull();
}
