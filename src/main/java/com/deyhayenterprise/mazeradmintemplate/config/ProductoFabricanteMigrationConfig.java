package com.deyhayenterprise.mazeradmintemplate.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.Fabricante;
import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.repository.FabricanteRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ProductoFabricanteMigrationConfig {

    private final ProductoRepository productoRepository;
    private final FabricanteRepository fabricanteRepository;
    private final JdbcTemplate jdbcTemplate;

    @Bean
    @Order(30)
    CommandLineRunner migrateProductosSinFabricante() {
        return args -> {
            ensureFabricanteColumn();
            migrate();
        };
    }

    private void ensureFabricanteColumn() {
        Integer exists = jdbcTemplate.queryForObject(
                """
                select count(*)
                from information_schema.columns
                where table_name = 'productos'
                  and column_name = 'fabricante_id'
                """,
                Integer.class
        );

        if (exists == null || exists == 0) {
            jdbcTemplate.execute("alter table productos add column fabricante_id bigint");
            log.info("Migracion productos-fabricante: columna fabricante_id creada en productos.");
        }
    }

    @Transactional
    public void migrate() {
        List<Producto> sinFabricante = productoRepository.findAllByFabricanteIsNull();
        if (sinFabricante.isEmpty()) {
            return;
        }

        Fabricante fallback = fabricanteRepository.findAllByActivoTrueOrderByIdDesc().stream()
                .findFirst()
                .orElseGet(this::createFallbackFabricante);

        for (Producto p : sinFabricante) {
            p.setFabricante(fallback);
            log.info("Migracion productos-fabricante: producto {} asignado a fabricante {}", p.getCodigo(), fallback.getNombre());
        }

        productoRepository.saveAll(sinFabricante);
    }

    private Fabricante createFallbackFabricante() {
        Fabricante f = new Fabricante();
        f.setNombre("Fabricante Migracion");
        f.setEmail("migracion.fabricante@mazer.local");
        f.setTelefono("000-0000");
        f.setPais("Local");
        f.setDireccion("Generado automaticamente");
        f.setActivo(true);
        Fabricante saved = fabricanteRepository.save(f);
        log.warn("Migracion productos-fabricante: creado fabricante fallback con id {}", saved.getId());
        return saved;
    }
}
