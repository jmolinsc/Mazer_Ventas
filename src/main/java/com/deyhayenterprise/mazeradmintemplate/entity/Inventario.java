package com.deyhayenterprise.mazeradmintemplate.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inventario")
@Getter
@Setter
@NoArgsConstructor
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(length = 20)
    private String estatus;

    @Column(length = 100)
    private String mov;

    @Column(length = 50)
    private String movid;

    @Column(length = 100)
    private String comportamiento;

    @Column(name = "total_cantidad", nullable = false)
    private Integer totalCantidad = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movtipo_id")
    private Movtipo movtipo;

    @OneToMany(mappedBy = "inventario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InventarioDetalle> detalles = new ArrayList<>();

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }
}

