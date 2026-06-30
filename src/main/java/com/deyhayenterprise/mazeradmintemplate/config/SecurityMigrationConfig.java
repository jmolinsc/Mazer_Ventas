package com.deyhayenterprise.mazeradmintemplate.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.AppMenu;
import com.deyhayenterprise.mazeradmintemplate.entity.MenuOption;
import com.deyhayenterprise.mazeradmintemplate.entity.Role;
import com.deyhayenterprise.mazeradmintemplate.repository.AppMenuRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.MenuOptionRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Migra opciones de menu que no existian al momento del seed inicial.
 * Es idempotente: comprueba existencia por codigo antes de insertar.
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityMigrationConfig {

    private final MenuOptionRepository menuOptionRepository;
    private final AppMenuRepository appMenuRepository;
    private final RoleRepository roleRepository;

    @Bean
    @Order(20)
    CommandLineRunner migrateMenuOptions() {
        return args -> migrate();
    }

    @Transactional
    public void migrate() {
        Map<String, AppMenu> menus = appMenuRepository.findAllByActivoTrueOrderByOrdenVisualAsc()
                .stream()
                .collect(LinkedHashMap::new, (m, menu) -> m.put(menu.getCodigo(), menu), Map::putAll);

        if (!menus.containsKey("MOVIMIENTOS")) {
            AppMenu movimientos = new AppMenu("MOVIMIENTOS", "Movimientos", "Configuración", "bi bi-file-earmark-text", 9);
            appMenuRepository.save(movimientos);
            menus.put(movimientos.getCodigo(), movimientos);
            log.info("Migracion: menu MOVIMIENTOS creado.");
        }

        Map<String, MenuOption> existing = menuOptionRepository.findAll()
                .stream()
                .collect(LinkedHashMap::new, (m, o) -> m.put(o.getCodigo(), o), Map::putAll);

        record OptionDef(String codigo, String nombre, String url, String descripcion, int orden, String menuCodigo) {}

        List<OptionDef> toAdd = List.of(
                new OptionDef("CLIENTES_EDITAR",     "Editar Cliente",     "/clientes/editar",     "Editar datos del cliente",    4, "CLIENTES"),
                new OptionDef("CLIENTES_ELIMINAR",   "Eliminar Cliente",   "/clientes/eliminar",   "Eliminar cliente",            5, "CLIENTES"),
                new OptionDef("PRODUCTOS_NUEVO",      "Nuevo Producto",      "/productos/nuevo",      "Alta de producto",             1, "PRODUCTOS"),
                new OptionDef("PRODUCTOS_LISTAR",     "Listar Productos",    "/productos/listar",     "Listado de productos",        2, "PRODUCTOS"),
                new OptionDef("PRODUCTOS_CATEGORIAS", "Categorias",          "/productos/categorias", "Clasificacion de productos",  3, "PRODUCTOS"),
                new OptionDef("PRODUCTOS_EDITAR",     "Editar Producto",     "/productos/editar",     "Editar datos del producto",  4, "PRODUCTOS"),
                new OptionDef("PRODUCTOS_ELIMINAR",   "Eliminar Producto",   "/productos/eliminar",   "Eliminar producto",          5, "PRODUCTOS"),
                new OptionDef("FABRICANTES_EDITAR",  "Editar Fabricante",  "/fabricantes/editar",  "Editar datos del fabricante", 3, "FABRICANTES"),
                new OptionDef("FABRICANTES_ELIMINAR", "Eliminar Fabricante", "/fabricantes/eliminar", "Eliminar fabricante",        4, "FABRICANTES"),
                new OptionDef("VENTAS_EDITAR",       "Editar Venta",       "/ventas/editar",       "Editar venta",               4, "VENTAS"),
                new OptionDef("VENTAS_AFECTAR",     "Afectar Venta",      "/ventas/afectar",      "Afectar venta",              5, "VENTAS"),
                new OptionDef("VENTAS_CANCELAR",    "Cancelar Venta",     "/ventas/cancelar",     "Cancelar venta",             6, "VENTAS"),
                new OptionDef("CONFIG_MENUS",       "Menus",              "/config/menus",        "Administracion de menus",    4, "CONFIGURACION"),
                new OptionDef("CONFIG_MOVIMIENTOS_MODULOS", "Modulo", "/config/movimientos/modulos", "Mantenimiento de módulos", 1, "MOVIMIENTOS"),
                new OptionDef("CONFIG_MOVIMIENTOS_COMPORTAMIENTOS", "Comportamiento", "/config/movimientos/comportamientos", "Mantenimiento de comportamientos", 2, "MOVIMIENTOS"),
                new OptionDef("CONFIG_MOVIMIENTOS_MOVTIPOS", "Movtipo", "/config/movimientos/movtipos", "Mantenimiento de tipos de movimiento", 3, "MOVIMIENTOS"),
                new OptionDef("EMPRESAS_EDITAR",     "Editar Empresa",      "/config/empresa",       "Editar datos de la empresa",    1, "EMPRESAS"),
                new OptionDef("EMPRESAS_CONFIG",     "Configurar",          "/config/empresa",       "Configuración de la empresa",   2, "EMPRESAS")
        );

        for (OptionDef def : toAdd) {
            if (existing.containsKey(def.codigo())) {
                continue;
            }
            AppMenu menu = menus.get(def.menuCodigo());
            if (menu == null) {
                log.warn("Migracion: menu {} no encontrado, omitiendo opcion {}", def.menuCodigo(), def.codigo());
                continue;
            }
            MenuOption option = new MenuOption(def.codigo(), def.nombre(), def.url(), def.descripcion(), def.orden(), menu);
            menuOptionRepository.save(option);
            log.info("Migracion: opcion {} ({}) creada.", def.codigo(), def.url());
            existing.put(def.codigo(), option);
        }

        // Genera permisos de accion por modulo para exponerlos en Configuracion > Permisos.
        for (AppMenu menu : menus.values()) {
            if ("DASHBOARD".equalsIgnoreCase(menu.getCodigo())) {
                continue;
            }
            ensureActionOption(menu, "AFECTAR", existing);
            ensureActionOption(menu, "CANCELAR", existing);
        }

        // Usar findAllWithOptions para cargar roles con fetch de options dentro de la misma sesion
        Map<String, Role> roles = roleRepository.findAllWithOptions()
                .stream()
                .collect(LinkedHashMap::new, (m, r) -> m.put(r.getCodigo().toUpperCase(), r), Map::putAll);

        assignOptions(roles.get("ADMIN"),
                List.of(
                        "EMPRESAS_EDITAR",
                        "EMPRESAS_CONFIG","CLIENTES_EDITAR", "CLIENTES_ELIMINAR", "PRODUCTOS_NUEVO", "PRODUCTOS_LISTAR", "PRODUCTOS_CATEGORIAS", "PRODUCTOS_EDITAR", "PRODUCTOS_ELIMINAR",
                        "FABRICANTES_EDITAR", "FABRICANTES_ELIMINAR", "VENTAS_EDITAR", "VENTAS_AFECTAR", "VENTAS_CANCELAR", "CONFIG_MENUS",
                        "CONFIG_MOVIMIENTOS_MODULOS", "CONFIG_MOVIMIENTOS_COMPORTAMIENTOS", "CONFIG_MOVIMIENTOS_MOVTIPOS",
                        "INVENTARIO_AFECTAR", "INVENTARIO_CANCELAR"),
                existing);

        assignOptions(roles.get("VENDEDOR"),
                List.of("PRODUCTOS_LISTAR", "VENTAS_EDITAR", "VENTAS_AFECTAR", "VENTAS_CANCELAR"),
                existing);

        assignOptions(roles.get("BODEGA"),
                List.of("PRODUCTOS_NUEVO", "PRODUCTOS_LISTAR", "PRODUCTOS_CATEGORIAS", "PRODUCTOS_EDITAR", "PRODUCTOS_ELIMINAR",
                        "INVENTARIO_AFECTAR", "INVENTARIO_CANCELAR"),
                existing);
    }

    private void assignOptions(Role role, List<String> codigos, Map<String, MenuOption> existing) {
        if (role == null) return;
        boolean changed = false;
        for (String codigo : codigos) {
            MenuOption opt = existing.get(codigo);
            if (opt != null && !role.getOptions().contains(opt)) {
                role.getOptions().add(opt);
                changed = true;
                log.info("Migracion: opcion {} asignada al rol {}.", codigo, role.getCodigo());
            }
        }
        if (changed) {
            roleRepository.save(role);
        }
    }

    private void ensureActionOption(AppMenu menu, String actionSuffix, Map<String, MenuOption> existing) {
        String menuCode = menu.getCodigo().toUpperCase(Locale.ROOT);
        String codigo = menuCode + "_" + actionSuffix;
        if (existing.containsKey(codigo)) {
            return;
        }

        String urlBase = "/" + menuCode.toLowerCase(Locale.ROOT).replace('_', '-');
        String actionPath = actionSuffix.toLowerCase(Locale.ROOT);
        String nombre = capitalize(actionPath) + " " + menu.getNombre();
        String descripcion = capitalize(actionPath) + " en modulo " + menu.getNombre();

        MenuOption option = new MenuOption(codigo, nombre, urlBase + "/" + actionPath, descripcion, 900, menu);
        menuOptionRepository.save(option);
        existing.put(codigo, option);
        log.info("Migracion: opcion {} ({}) creada.", codigo, option.getUrl());
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        return value.substring(0, 1).toUpperCase(Locale.ROOT) + value.substring(1);
    }
}
