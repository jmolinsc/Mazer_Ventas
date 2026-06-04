package com.deyhayenterprise.mazeradmintemplate.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 140)
    private String nombre;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(length = 30)
    private String telefono;

    @Column(nullable = false, length = 40)
    private String categoria;

    @Column(length = 255)
    private String direccion;

    @Column(name = "limite_credito", precision = 14, scale = 2)
    private BigDecimal limiteCredito;

    @Column(length = 120)
    private String agente;

    @Column(name = "dias_credito")
    private Integer diasCredito;

    @Column(length = 25)
    private String nit;

    @Column(length = 25)
    private String nrc;

    @Column(name = "actividad_economica", length = 255)
    private String actividadEconomica;

    @Column(length = 80)
    private String pais;

    @Column(length = 80)
    private String departamento;

    @Column(length = 100)
    private String municipio;

    @Column(nullable = false)
    private boolean activo = true;
}

