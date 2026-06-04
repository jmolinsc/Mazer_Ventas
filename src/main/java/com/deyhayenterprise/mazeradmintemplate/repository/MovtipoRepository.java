package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.deyhayenterprise.mazeradmintemplate.entity.Movtipo;

public interface MovtipoRepository extends JpaRepository<Movtipo, Long> {

    @EntityGraph(attributePaths = {"comportamiento", "comportamiento.modulo"})
    List<Movtipo> findAllByActivoTrueOrderByNombreAsc();

    @EntityGraph(attributePaths = {"comportamiento", "comportamiento.modulo"})
    Optional<Movtipo> findByIdAndActivoTrue(Long id);

    @EntityGraph(attributePaths = {"comportamiento", "comportamiento.modulo"})
    @Query("""
            select distinct m
            from Role r
            join r.users u
            join r.movtipos m
            where lower(u.username) = lower(:username)
              and u.activo = true
              and r.activo = true
              and m.activo = true
              and m.comportamiento.activo = true
              and m.comportamiento.modulo.activo = true
              and upper(m.comportamiento.modulo.nombre) = upper(:modulo)
            order by m.nombre asc
            """)
    List<Movtipo> findAllowedByUsernameAndModulo(@Param("username") String username,
                                                 @Param("modulo") String modulo);

    boolean existsByNombreIgnoreCaseAndComportamientoIdAndActivoTrue(String nombre, Long comportamientoId);

    boolean existsByNombreIgnoreCaseAndComportamientoIdAndIdNot(String nombre, Long comportamientoId, Long id);
}

