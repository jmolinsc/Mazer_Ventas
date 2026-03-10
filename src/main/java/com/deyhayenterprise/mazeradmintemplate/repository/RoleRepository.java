package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.deyhayenterprise.mazeradmintemplate.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByCodigoIgnoreCase(String codigo);

    @EntityGraph(attributePaths = "options")
    @Query("select distinct r from Role r left join fetch r.options order by r.nombre asc")
    List<Role> findAllWithOptions();
}

