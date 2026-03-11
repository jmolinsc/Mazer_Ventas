package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.UnidadMedida;

public interface UnidadMedidaRepository extends JpaRepository<UnidadMedida, Long> {

    List<UnidadMedida> findAllByActivoTrueOrderByNombreAsc();

    Optional<UnidadMedida> findByIdAndActivoTrue(Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);

    boolean existsByNombreIgnoreCaseAndActivoTrue(String nombre);

    Optional<UnidadMedida> findByNombreIgnoreCaseAndActivoTrue(String nombre);
}

