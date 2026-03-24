package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.Modulo;

public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    List<Modulo> findAllByActivoTrueOrderByNombreAsc();

    Optional<Modulo> findByIdAndActivoTrue(Long id);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}

