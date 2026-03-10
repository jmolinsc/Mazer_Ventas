package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.deyhayenterprise.mazeradmintemplate.entity.AppMenu;

public interface AppMenuRepository extends JpaRepository<AppMenu, Long> {

    @EntityGraph(attributePaths = "options")
    List<AppMenu> findAllByActivoTrueOrderByOrdenVisualAsc();
}

