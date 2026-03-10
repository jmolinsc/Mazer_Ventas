# Módulo de Seguridad Dinámica — Documentación Técnica

## Objetivo

Implementar autenticación y autorización completamente basada en base de datos PostgreSQL, con un menú lateral (sidebar) que se genera dinámicamente según las opciones asignadas al rol del usuario autenticado. Ninguna ruta ni opción de menú está hardcodeada en el código.

---

## Arquitectura general

```
Navegador → Spring Security Filter Chain
               ↓
          DatabaseMenuAuthorizationManager
               ↓ consulta MenuOptionRepository
          PostgreSQL (mazer_security)
               ↓
          Thymeleaf → sidebar generado desde sidebarSections (model attribute)
```

---

## Modelo de datos

### Tablas principales

| Tabla           | Entidad          | Descripción                                  |
|-----------------|------------------|----------------------------------------------|
| `usuarios`      | `AppUser`        | Usuarios del sistema con credenciales        |
| `roles`         | `Role`           | Roles de acceso (ADMIN, VENDEDOR, etc.)      |
| `menus`         | `AppMenu`        | Grupos/secciones del menú (Ventas, Config…)  |
| `opciones_menu` | `MenuOption`     | Opciones concretas con URL asociada          |

### Tablas de relación

| Tabla         | Relación               |
|---------------|------------------------|
| `usuario_rol` | AppUser ↔ Role (N:M)   |
| `rol_opcion`  | Role ↔ MenuOption (N:M)|

### Diagrama simplificado

```
AppUser ────── usuario_rol ────── Role
                                   │
                               rol_opcion
                                   │
AppMenu ←── menu_id ──────── MenuOption
```

---

## Capas del módulo

### Entidades (`entity/`)

| Clase        | Descripción                                               |
|-------------|-----------------------------------------------------------|
| `AppUser`   | Usuario: username, password (BCrypt), email, activo, bloqueado, roles |
| `Role`      | Rol: codigo, nombre, activo, opciones asignadas           |
| `AppMenu`   | Menú: codigo, nombre, seccion, icono, ordenVisual, activo |
| `MenuOption`| Opción: codigo, nombre, url, descripcion, ordenVisual, activo, menu |

### Repositorios (`repository/`)

| Clase                   | Método principal destacado                                 |
|-------------------------|------------------------------------------------------------|
| `AppUserRepository`     | `findByUsernameIgnoreCase`, `findAllWithRoles`             |
| `RoleRepository`        | `findAllWithOptions`                                       |
| `AppMenuRepository`     | `findAllByActivoTrueOrderByOrdenVisualAsc`                 |
| `MenuOptionRepository`  | `findVisibleOptionsByUsername`, `findAllowedUrlsByUsername` |

> `findVisibleOptionsByUsername` une usuarios, roles y opciones activas para armar el sidebar.  
> `findAllowedUrlsByUsername` devuelve solo URLs; la usa `DatabaseMenuAuthorizationManager`.

### Servicios (`service/`)

| Clase            | Responsabilidad                                                    |
|------------------|--------------------------------------------------------------------|
| `MenuService`    | Construye `List<SidebarSectionView>` agrupadas por sección y menú  |
| `AppUserService` | CRUD de usuarios: listar, buscar, crear con roles asignados        |
| `RoleService`    | CRUD de roles: listar, asignar/quitar opciones de menú             |

### DTOs del sidebar (`service/dto/`)

| Record / Clase          | Uso                                                         |
|-------------------------|-------------------------------------------------------------|
| `SidebarSectionView`    | Sección con título y lista de menús                         |
| `SidebarMenuView`       | Menú con icono, título, url (si es directo) y sub-opciones  |
| `SidebarOptionView`     | Opción de submenú con título y url                          |
| `MenuOptionsGroupView`  | Catálogo de opciones agrupadas para la pantalla de permisos |
| `OptionPermissionView`  | Opción individual para asignación de permisos               |

### Seguridad (`security/` y `config/SecurityConfig`)

| Clase                              | Responsabilidad                                                    |
|------------------------------------|--------------------------------------------------------------------|
| `CustomUserDetailsService`         | Carga usuario desde DB y convierte roles en `GrantedAuthority`s    |
| `DatabaseMenuAuthorizationManager` | Evalúa cada request contra las URLs autorizadas del usuario en DB  |
| `SecurityConfig`                   | Configura el `SecurityFilterChain`: login, logout, 403, permisos   |

### Controladores (`controller/`)

| Clase            | Rutas manejadas                                         |
|------------------|---------------------------------------------------------|
| `WebController`  | `/`, `/index`, `/auth-login`, todas las rutas de layout |
| `ConfigController` | `/config/empresa`, `/config/usuarios`, `/config/permisos` |

### Advice global (`web/GlobalModelAttributes`)

Inyecta automáticamente en **todos** los modelos de vistas autenticadas:
- `sidebarSections` → lista de secciones para el sidebar
- `currentUser` → entidad `AppUser` del usuario en sesión
- `currentPath` → ruta actual normalizada para marcar activo en el menú

---

## Configuración

### `application.properties` (producción/desarrollo)

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/mazer_security}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
spring.jpa.hibernate.ddl-auto=update
```

Las tablas se crean automáticamente con `ddl-auto=update` en el primer arranque.

### `application-test.properties` (pruebas)

```properties
spring.datasource.url=jdbc:h2:mem:mazerdb;MODE=PostgreSQL
spring.jpa.hibernate.ddl-auto=create-drop
```

---

## Seed de datos iniciales (`SecuritySeedDataConfig`)

Al arrancar por primera vez (cuando las tablas están vacías) se crean automáticamente:

### Menús
- Dashboard, Ventas, Inventario, Clientes, Productos, Fabricantes, Cuentas por Cobrar, Configuración

### Opciones por menú
21 opciones en total, cada una con su URL real.

### Roles
| Rol        | Opciones asignadas                                         |
|------------|------------------------------------------------------------|
| ADMIN      | Todas (21 opciones)                                        |
| VENDEDOR   | Dashboard, Ventas (3), Clientes (2), Productos (listar), CxC (2) |
| BODEGA     | Dashboard, Inventario (3), Productos (3), Fabricantes (2)  |
| CONTADOR   | Dashboard, Ventas (reportes), CxC (pendientes, reportes), Config empresa |

### Usuarios de prueba
| Usuario    | Contraseña    | Rol       | Estado   |
|------------|--------------|-----------|----------|
| `admin`    | `Admin123*`  | ADMIN     | Activo   |
| `vendedor` | `Ventas123*` | VENDEDOR  | Activo   |
| `bodega`   | `Bodega123*` | BODEGA    | Activo   |
| `contador` | `Conta123*`  | CONTADOR  | Inactivo |

---

## Flujo de autorización

```
1. Usuario hace GET /ventas/nueva
2. Spring Security intercepta → DatabaseMenuAuthorizationManager.check()
3. Se extrae username del Authentication
4. MenuOptionRepository.findAllowedUrlsByUsername(username) → ["/ index", "/ventas/nueva", ...]
5. Se normaliza y compara la ruta solicitada con las permitidas
6. Si match → AuthorizationDecision(true) → pasa
7. Si no match → AuthorizationDecision(false) → Spring envía a /error-403
```

---

## Flujo del sidebar dinámico

```
1. Cualquier request a un @Controller pasa por GlobalModelAttributes
2. Se llama MenuService.buildSidebarForUser(username)
3. MenuOptionRepository.findVisibleOptionsByUsername(username) devuelve opciones activas con sus menús
4. Se agrupan por sección → SidebarSectionView > SidebarMenuView > SidebarOptionView
5. Thymeleaf itera sidebarSections en fragments/sidebar.html
6. Cada menú/opción se marca active según currentPath
```

---

## Vistas del módulo de administración

| URL                  | Vista                      | Descripción                                    |
|----------------------|----------------------------|------------------------------------------------|
| `/config/empresa`    | `config/empresa.html`      | Datos corporativos + estado del módulo         |
| `/config/usuarios`   | `config/usuarios.html`     | Listado real de usuarios + formulario de alta  |
| `/config/permisos`   | `config/permisos.html`     | Asignación de opciones por rol con checkboxes  |

---

## Docker: levantamiento rápido

```bash
# Solo la base de datos (desarrollo local):
docker compose up postgres -d

# App completa dockerizada:
docker compose up -d
```

La app escucha en `http://localhost:8080`.  
Login: `http://localhost:8080/auth-login`

---

## Pruebas automatizadas

Archivo: `SecurityMenuIntegrationTests.java`

| Test                                    | Valida                                               |
|-----------------------------------------|------------------------------------------------------|
| `loginPageShouldBeAccessible`           | Login disponible sin autenticación                   |
| `adminShouldLoginAndAccessConfigUsers`  | Admin puede loguearse y acceder a usuarios           |
| `vendedorShouldSeeSalesButNotConfig`    | Vendedor accede a ventas pero recibe 403 en config   |
| `inactiveUserShouldNotLogin`            | Usuario inactivo no puede autenticarse               |

Ejecutar con perfil `test` (H2 en memoria):
```bash
./mvnw test
```

---

## Extensión del sistema

Para agregar un nuevo módulo:

1. **Insertar un `AppMenu`** con su código, nombre, sección e ícono (Bootstrap Icons).
2. **Insertar `MenuOption`s** asociadas al menú con sus URLs reales.
3. **Asignar las opciones al rol** correspondiente desde `/config/permisos`.
4. **Crear el `@Controller`** con los `@GetMapping` a las URLs registradas.
5. **Crear las vistas Thymeleaf** usando el fragmento `fragments/layout :: layout(...)`.

No se requiere modificar nada en `SecurityConfig`, `DatabaseMenuAuthorizationManager` ni `MenuService`.

