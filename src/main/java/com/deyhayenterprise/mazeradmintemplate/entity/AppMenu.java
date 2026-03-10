package com.deyhayenterprise.mazeradmintemplate.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "menus")
@Getter
@Setter
@NoArgsConstructor
public class AppMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 120)
    private String seccion;

    @Column(length = 80)
    private String icono;

    @Column(name = "orden_visual", nullable = false)
    private Integer ordenVisual = 0;

    @Column(nullable = false)
    private boolean activo = true;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MenuOption> options = new LinkedHashSet<>();

    public AppMenu(String codigo, String nombre, String seccion, String icono, Integer ordenVisual) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.seccion = seccion;
        this.icono = icono;
        this.ordenVisual = ordenVisual;
    }
}

