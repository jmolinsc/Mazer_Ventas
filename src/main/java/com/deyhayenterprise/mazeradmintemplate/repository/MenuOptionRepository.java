package com.deyhayenterprise.mazeradmintemplate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.deyhayenterprise.mazeradmintemplate.entity.MenuOption;

public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {

    @Query("""
            select distinct o
            from MenuOption o
            join fetch o.menu m
            join o.roles r
            join r.users u
            where lower(u.username) = lower(:username)
              and u.activo = true
              and r.activo = true
              and o.activo = true
              and m.activo = true
            order by m.ordenVisual asc, o.ordenVisual asc
            """)
    List<MenuOption> findVisibleOptionsByUsername(@Param("username") String username);

    @Query("""
            select distinct o.url
            from MenuOption o
            join o.roles r
            join r.users u
            where lower(u.username) = lower(:username)
              and u.activo = true
              and r.activo = true
              and o.activo = true
            """)
    List<String> findAllowedUrlsByUsername(@Param("username") String username);

    @Query("""
            select distinct o
            from MenuOption o
            join fetch o.menu m
            where o.activo = true
              and m.activo = true
            order by m.ordenVisual asc, o.ordenVisual asc
            """)
    List<MenuOption> findAllActiveWithMenu();
}

