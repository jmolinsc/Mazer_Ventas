# Sistema de Ventas - Estructura Implementada

## 📋 Descripción

Se implementó una estructura completa de un **Sistema de Gestión de Ventas** con un diseño modular usando:
- **Spring Boot 3.1.4**
- **Thymeleaf** con fragments reutilizables
- **Bootstrap 5** para el diseño responsive
- **Mazer Admin Template** como base UI

## 🏗️ Estructura de Directorios

### Controllers (6)
- `VentasController` - Gestión de ventas
- `InventarioController` - Control de inventario
- `ClientesController` - Administración de clientes
- `ProductosController` - Catálogo de productos
- `FabricantesController` - Base de fabricantes
- `CuentasCobrarController` - Cartera de clientes
- `ConfigController` - Configuración del sistema

### Templates (Fragmentos reutilizables)

#### Fragments Base (`src/main/resources/templates/fragments/`)
- `layout.html` - Layout principal orquestador
- `head.html` - Sección `<head>` con meta y estilos
- `sidebar.html` - Navegación lateral con menú completo
- `header.html` - Header superior
- `footer.html` - Footer
- `scripts.html` - Scripts individuales (themeInit, dark, scrollbar, app)

#### Vistas por Módulo

**Ventas** (`ventas/`)
- `nueva.html` - Crear nueva venta
- `listar.html` - Historial de ventas
- `reportes.html` - Análisis y reportes

**Inventario** (`inventario/`)
- `movimientos.html` - Entradas y salidas
- `existencias.html` - Stock disponible
- `ajustes.html` - Correcciones de inventario

**Clientes** (`clientes/`)
- `nuevo.html` - Registrar cliente
- `listar.html` - Base de datos de clientes
- `categorias.html` - Tipos de clientes

**Productos** (`productos/`)
- `nuevo.html` - Crear producto
- `listar.html` - Catálogo
- `categorias.html` - Categorías

**Fabricantes** (`fabricantes/`)
- `nuevo.html` - Registrar fabricante
- `listar.html` - Listado de fabricantes

**Cuentas por Cobrar** (`cuentas-cobrar/`)
- `pendientes.html` - Facturas pendientes
- `pagos.html` - Registrar pago
- `reportes.html` - Análisis de cartera

**Configuración** (`config/`)
- `empresa.html` - Datos de empresa
- `usuarios.html` - Gestión de usuarios
- `permisos.html` - Control de permisos

## 🎯 Rutas Disponibles

### Dashboard
- `GET /` → Dashboard principal
- `GET /index` → Dashboard

### Ventas
- `GET /ventas/nueva` → Crear venta
- `GET /ventas/listar` → Historial
- `GET /ventas/reportes` → Reportes

### Inventario
- `GET /inventario/movimientos` → Movimientos
- `GET /inventario/existencias` → Stock
- `GET /inventario/ajustes` → Ajustes

### Clientes
- `GET /clientes/nuevo` → Nuevo cliente
- `GET /clientes/listar` → Listar
- `GET /clientes/categorias` → Categorías

### Productos
- `GET /productos/nuevo` → Nuevo producto
- `GET /productos/listar` → Catálogo
- `GET /productos/categorias` → Categorías

### Fabricantes
- `GET /fabricantes/nuevo` → Nuevo
- `GET /fabricantes/listar` → Listar

### Cuentas por Cobrar
- `GET /cuentas-cobrar/pendientes` → Pendientes
- `GET /cuentas-cobrar/pagos` → Registrar pago
- `GET /cuentas-cobrar/reportes` → Reportes

### Configuración
- `GET /config/empresa` → Datos empresa
- `GET /config/usuarios` → Usuarios
- `GET /config/permisos` → Permisos

## 📐 Patrón Thymeleaf Fragments

### Usando el Layout en una Vista

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<body th:replace="~{fragments/layout :: layout(
    'Título Navegador',
    ${pageHeading},
    ${pageSubtitle},
    ~{::section}
)}">
<section class="section">
    <!-- Contenido específico de la página -->
</section>
</body>
</html>
```

### En el Controller

```java
@GetMapping("/ruta")
public String miPagina(Model model) {
    model.addAttribute("pageHeading", "Título");
    model.addAttribute("pageSubtitle", "Subtítulo");
    return "carpeta/archivo";
}
```

## 🎨 Componentes UI Incluidos

- Tablas con datos de ejemplo
- Formularios Bootstrap
- Cards con métricas
- Badges para estados
- Botones con acciones
- Breadcrumbs navegables
- Modal de ejemplo
- Navegación anidada en sidebar

## 🚀 Próximos Pasos

1. **Backend**: Implementar modelos JPA/Hibernate
   - `Cliente`, `Producto`, `Venta`, `DetalleVenta`
   - `Fabricante`, `Inventario`, `CuentaCobrar`

2. **Servicios**: Crear capas de servicio
   - `VentasService`, `InventarioService`, etc.

3. **APIs REST**: Endpoints para operaciones CRUD

4. **Base de Datos**: Configurar PostgreSQL/MySQL
   - Migrations con Flyway/Liquibase

5. **Validaciones**: Agregar validadores Bean Validation

6. **Seguridad**: Implementar Spring Security
   - Roles y permisos dinámicos

7. **Testing**: Tests unitarios e integración

## 📦 Dependencias Actuales

```xml
- spring-boot-starter-web
- spring-boot-starter-thymeleaf
- spring-boot-devtools
- lombok
- spring-boot-starter-test
```

## 🔧 Ejecución

```bash
# Compilar
./mvnw.cmd clean package -DskipTests

# Ejecutar
./mvnw.cmd spring-boot:run

# Acceder
http://localhost:8080
```

---

**Creado**: Marzo 2025
**Plantilla Base**: Mazer Admin Template v2.2.0

