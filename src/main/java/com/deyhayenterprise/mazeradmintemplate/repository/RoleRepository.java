package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.deyhayenterprise.mazeradmintemplate.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByCodigoIgnoreCase(String codigo);
    // 🔥 CAMBIA ESTO:
    Optional<Role> findByNombre(String nombre);  // ← Antes era findBy(String nombre)
    @EntityGraph(attributePaths = {"options", "movtipos"})
    @Query("select distinct r from Role r left join fetch r.options left join fetch r.movtipos order by r.nombre asc")
    List<Role> findAllWithOptions();
}
