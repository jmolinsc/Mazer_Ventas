package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.AppMenu;

public interface AppMenuRepository extends JpaRepository<AppMenu, Long> {

    @EntityGraph(attributePaths = "options")
    List<AppMenu> findAllByActivoTrueOrderByOrdenVisualAsc();

    @EntityGraph(attributePaths = "options")
    List<AppMenu> findAllByOrderByOrdenVisualAsc();

    boolean existsByCodigoIgnoreCase(String codigo);

    Optional<AppMenu> findByIdAndActivoTrue(Long id);
}
