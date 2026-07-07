package com.deyhayenterprise.mazeradmintemplate.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.deyhayenterprise.mazeradmintemplate.entity.AppMenu;
import com.deyhayenterprise.mazeradmintemplate.entity.AppUser;
import com.deyhayenterprise.mazeradmintemplate.entity.MenuOption;
import com.deyhayenterprise.mazeradmintemplate.entity.Role;
import com.deyhayenterprise.mazeradmintemplate.repository.AppMenuRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.AppUserRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.MenuOptionRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.RoleRepository;

@Configuration
public class SecuritySeedDataConfig {

    @Bean
    CommandLineRunner seedSecurityData(AppMenuRepository appMenuRepository,
                                       MenuOptionRepository menuOptionRepository,
                                       RoleRepository roleRepository,
                                       AppUserRepository appUserRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            Map<String, AppMenu> menus = createMenus(appMenuRepository);
            Map<String, MenuOption> options = createOptions(menuOptionRepository, menus);
            Map<String, Role> roles = createRoles(roleRepository, options);
            createUsers(appUserRepository, passwordEncoder, roles);
        };
    }

    private Map<String, AppMenu> createMenus(AppMenuRepository repository) {
        if (repository.count() > 0) {
            return repository.findAllByActivoTrueOrderByOrdenVisualAsc().stream()
                    .collect(LinkedHashMap::new, (map, menu) -> map.put(menu.getCodigo(), menu), Map::putAll);
        }

        List<AppMenu> menus = List.of(
                new AppMenu("DASHBOARD", "Dashboard", "Menu Principal", "bi bi-grid-fill", 1,true),
                new AppMenu("VENTAS", "Ventas", "Sistema de Ventas", "bi bi-cart-fill", 2,true),
                new AppMenu("INVENTARIO", "Inventario", "Sistema de Ventas", "bi bi-box-seam", 3,true),
                new AppMenu("CLIENTES", "Clientes", "Sistema de Ventas", "bi bi-person-circle", 4,true),
                new AppMenu("PRODUCTOS", "Productos", "Sistema de Ventas", "bi bi-tag-fill", 5,true),
                new AppMenu("FABRICANTES", "Fabricantes", "Sistema de Ventas", "bi bi-hammer", 6,true),
                new AppMenu("CUENTAS_COBRAR", "Cuentas por Cobrar", "Sistema de Ventas", "bi bi-cash-coin", 7,true),
                // ✅ NUEVO: Menú EMPRESAS
                new AppMenu("EMPRESAS", "Empresas", "Configuración", "bi bi-building", 8,true),
                new AppMenu("CONFIGURACION", "Configuración", "Configuración", "bi bi-gear-fill", 9,true),
                new AppMenu("MOVIMIENTOS", "Movimientos", "Configuración", "bi bi-file-earmark-text", 10,true)
        );

        repository.saveAll(menus);
        return repository.findAllByActivoTrueOrderByOrdenVisualAsc().stream()
                .collect(LinkedHashMap::new, (map, menu) -> map.put(menu.getCodigo(), menu), Map::putAll);
    }

    private Map<String, MenuOption> createOptions(MenuOptionRepository repository, Map<String, AppMenu> menus) {
        if (repository.count() == 0) {
            repository.saveAll(List.of(
                    // Dashboard
                    new MenuOption("DASHBOARD_HOME", "Dashboard", "/index", "Pantalla principal", 1, menus.get("DASHBOARD"),true),

                    // Ventas
                    new MenuOption("VENTAS_NUEVA", "Nueva Venta", "/ventas/nueva", "Registrar venta", 1, menus.get("VENTAS"),   true),
                    new MenuOption("VENTAS_AFECTAR", "Afectar Venta", "/ventas/afectar", "Afectar venta", 2, menus.get("VENTAS"),false),
                    new MenuOption("VENTAS_CANCELAR", "Cancelar Venta", "/ventas/cancelar", "Cancelar venta", 3, menus.get("VENTAS"),false),
                    new MenuOption("VENTAS_LISTAR", "Listar Ventas", "/ventas/listar", "Consultar ventas", 4, menus.get("VENTAS"),true),
                    new MenuOption("VENTAS_REPORTES", "Reportes de Ventas", "/ventas/reportes", "Analítica comercial", 5, menus.get("VENTAS"),true),
                    new MenuOption("VENTAS_EDITAR", "Editar Venta", "/ventas/editar", "Editar venta", 6, menus.get("VENTAS"),false),

                    // Inventario
                    new MenuOption("INVENTARIO_MOV", "Movimientos", "/inventario/movimientos", "Entradas y salidas", 1, menus.get("INVENTARIO"),true),
                    new MenuOption("INVENTARIO_EXIST", "Existencias", "/inventario/existencias", "Consulta de stock", 2, menus.get("INVENTARIO"),true),
                    new MenuOption("INVENTARIO_AJUST", "Ajustes", "/inventario/ajustes", "Ajustes de inventario", 3, menus.get("INVENTARIO"),true),

                    // Clientes
                    new MenuOption("CLIENTES_NUEVO", "Nuevo Cliente", "/clientes/nuevo", "Alta de cliente", 1, menus.get("CLIENTES"),true),
                    new MenuOption("CLIENTES_LISTAR", "Listar Clientes", "/clientes/listar", "Listado de clientes", 2, menus.get("CLIENTES"),true),
                    new MenuOption("CLIENTES_CATEGORIAS", "Categorías", "/clientes/categorias", "Clasificación de clientes", 3, menus.get("CLIENTES"),false),
                    new MenuOption("CLIENTES_EDITAR", "Editar Cliente", "/clientes/editar", "Editar datos del cliente", 4, menus.get("CLIENTES"),false),
                    new MenuOption("CLIENTES_ELIMINAR", "Eliminar Cliente", "/clientes/eliminar", "Eliminar cliente", 5, menus.get("CLIENTES"),false),

                    // Productos
                    new MenuOption("PRODUCTOS_NUEVO", "Nuevo Producto", "/productos/nuevo", "Alta de producto", 1, menus.get("PRODUCTOS"),false),
                    new MenuOption("PRODUCTOS_LISTAR", "Listar Productos", "/productos/listar", "Listado de productos", 2, menus.get("PRODUCTOS"),true),
                    new MenuOption("PRODUCTOS_CATEGORIAS", "Categorías", "/productos/categorias", "Clasificación de productos", 3, menus.get("PRODUCTOS"),false),
                    new MenuOption("PRODUCTOS_EDITAR", "Editar Producto", "/productos/editar", "Editar datos del producto", 4, menus.get("PRODUCTOS"),false),
                    new MenuOption("PRODUCTOS_ELIMINAR", "Eliminar Producto", "/productos/eliminar", "Eliminar producto", 5, menus.get("PRODUCTOS"),false),

                    // Fabricantes
                    new MenuOption("FABRICANTES_NUEVO", "Nuevo Fabricante", "/fabricantes/nuevo", "Alta de fabricante", 1, menus.get("FABRICANTES"),false),
                    new MenuOption("FABRICANTES_LISTAR", "Listar Fabricantes", "/fabricantes/listar", "Listado de fabricantes", 2, menus.get("FABRICANTES"),true),

                    // Cuentas por Cobrar
                    new MenuOption("COBRAR_PENDIENTES", "Pendientes", "/cuentas-cobrar/pendientes", "Cuentas por cobrar pendientes", 1, menus.get("CUENTAS_COBRAR"),true),
                    new MenuOption("COBRAR_PAGOS", "Registrar Pago", "/cuentas-cobrar/pagos", "Registrar pagos", 2, menus.get("CUENTAS_COBRAR"),true),
                    new MenuOption("COBRAR_REPORTES", "Reportes", "/cuentas-cobrar/reportes", "Analítica de cartera", 3, menus.get("CUENTAS_COBRAR"),true),

                    // ✅ NUEVO: Opciones del menú EMPRESAS
                    new MenuOption("EMPRESAS_EDITAR", "Editar Empresa", "/config/empresa", "Editar datos de la empresa", 1, menus.get("EMPRESAS"),true),
                    new MenuOption("EMPRESAS_CONFIG", "Configurar", "/config/empresa", "Configuración de la empresa", 2, menus.get("EMPRESAS"),true),
                    new MenuOption("EMPRESAS_LISTAR", "Listar Empresas", "/empresas/listar", "Listado de empresas", 3, menus.get("EMPRESAS"),true),

                    // Configuración
                    new MenuOption("CONFIG_EMPRESA", "Datos Empresa", "/config/empresa", "Datos corporativos", 1, menus.get("CONFIGURACION"),true),
                    new MenuOption("CONFIG_USUARIOS", "Usuarios", "/config/usuarios", "Administración de usuarios", 2, menus.get("CONFIGURACION"),true),
                    new MenuOption("CONFIG_PERMISOS", "Permisos", "/config/permisos", "Asignación de permisos", 3, menus.get("CONFIGURACION"),true),
                    new MenuOption("CONFIG_MENUS", "Menus", "/config/menus", "Administración de menús", 4, menus.get("CONFIGURACION"),true),

                    // Movimientos
                    new MenuOption("CONFIG_MOVIMIENTOS_MODULOS", "Modulo", "/config/movimientos/modulos", "Mantenimiento de módulos", 1, menus.get("MOVIMIENTOS"),true),
                    new MenuOption("CONFIG_MOVIMIENTOS_COMPORTAMIENTOS", "Comportamiento", "/config/movimientos/comportamientos", "Mantenimiento de comportamientos", 2, menus.get("MOVIMIENTOS"),true),
                    new MenuOption("CONFIG_MOVIMIENTOS_MOVTIPOS", "Movtipo", "/config/movimientos/movtipos", "Mantenimiento de tipos de movimiento", 3, menus.get("MOVIMIENTOS"),true)
            ));
        }

        return repository.findAll().stream()
                .collect(LinkedHashMap::new, (map, option) -> map.put(option.getCodigo(), option), Map::putAll);
    }

    private Map<String, Role> createRoles(RoleRepository repository, Map<String, MenuOption> options) {
        if (repository.count() > 0) {
            return repository.findAllWithOptions().stream()
                    .collect(LinkedHashMap::new, (map, role) -> map.put(role.getCodigo(), role), Map::putAll);
        }

        // ✅ ADMIN - Acceso completo
        Role admin = new Role("ADMIN", "Administrador", "Acceso completo al sistema");
        admin.getOptions().addAll(options.values());

        // ✅ VENDEDOR - Ventas y clientes
        Role vendedor = new Role("VENDEDOR", "Vendedor", "Opera ventas y clientes");
        vendedor.getOptions().addAll(Set.of(
                options.get("DASHBOARD_HOME"),
                options.get("VENTAS_NUEVA"),
                options.get("VENTAS_AFECTAR"),
                options.get("VENTAS_CANCELAR"),
                options.get("VENTAS_LISTAR"),
                options.get("VENTAS_REPORTES"),
                options.get("VENTAS_EDITAR"),
                options.get("CLIENTES_NUEVO"),
                options.get("CLIENTES_LISTAR"),
                options.get("PRODUCTOS_LISTAR"),
                options.get("COBRAR_PENDIENTES"),
                options.get("COBRAR_PAGOS")
        ));

        // ✅ BODEGA - Inventario y productos
        Role bodega = new Role("BODEGA", "Bodega", "Controla inventario y fabricantes");
        bodega.getOptions().addAll(Set.of(
                options.get("DASHBOARD_HOME"),
                options.get("INVENTARIO_MOV"),
                options.get("INVENTARIO_EXIST"),
                options.get("INVENTARIO_AJUST"),
                options.get("PRODUCTOS_NUEVO"),
                options.get("PRODUCTOS_LISTAR"),
                options.get("PRODUCTOS_CATEGORIAS"),
                options.get("FABRICANTES_NUEVO"),
                options.get("FABRICANTES_LISTAR")
        ));

        // ✅ CONTADOR - Reportes y cartera
        Role contador = new Role("CONTADOR", "Contador", "Consulta reportes y cartera");
        contador.getOptions().addAll(Set.of(
                options.get("DASHBOARD_HOME"),
                options.get("VENTAS_REPORTES"),
                options.get("COBRAR_PENDIENTES"),
                options.get("COBRAR_REPORTES"),
                options.get("CONFIG_EMPRESA"),
                options.get("EMPRESAS_LISTAR")  // ✅ Puede ver empresas
        ));

        repository.saveAll(List.of(admin, vendedor, bodega, contador));
        return repository.findAllWithOptions().stream()
                .collect(LinkedHashMap::new, (map, role) -> map.put(role.getCodigo(), role), Map::putAll);
    }

    private void createUsers(AppUserRepository repository, PasswordEncoder passwordEncoder, Map<String, Role> roles) {
        if (repository.count() > 0) {
            return;
        }

        AppUser admin = new AppUser("admin", passwordEncoder.encode("Admin123*"), "admin@mazer.local", "Administrador General");
        admin.getRoles().add(roles.get("ADMIN"));

        AppUser vendedor = new AppUser("vendedor", passwordEncoder.encode("Ventas123*"), "vendedor@mazer.local", "Usuario Vendedor");
        vendedor.getRoles().add(roles.get("VENDEDOR"));

        AppUser bodega = new AppUser("bodega", passwordEncoder.encode("Bodega123*"), "bodega@mazer.local", "Usuario Bodega");
        bodega.getRoles().add(roles.get("BODEGA"));

        AppUser contador = new AppUser("contador", passwordEncoder.encode("Conta123*"), "contador@mazer.local", "Usuario Contador");
        contador.getRoles().add(roles.get("CONTADOR"));
        contador.setActivo(false);

        repository.saveAll(List.of(admin, vendedor, bodega, contador));
    }
}