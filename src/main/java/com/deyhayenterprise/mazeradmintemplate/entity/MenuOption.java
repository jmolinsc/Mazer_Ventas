package com.deyhayenterprise.mazeradmintemplate.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "opciones_menu")
@Getter
@Setter
@NoArgsConstructor
public class MenuOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 180)
    private String url;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "orden_visual", nullable = false)
    private Integer ordenVisual = 0;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_id", nullable = false)
    private AppMenu menu;

    @ManyToMany(mappedBy = "options")
    private Set<Role> roles = new LinkedHashSet<>();

    public MenuOption(String codigo, String nombre, String url, String descripcion, Integer ordenVisual, AppMenu menu) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.url = url;
        this.descripcion = descripcion;
        this.ordenVisual = ordenVisual;
        this.menu = menu;
    }
}

