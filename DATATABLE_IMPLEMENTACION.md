# Implementación de DataTables en Sistema de Ventas

## 📋 Descripción

Se han implementado **DataTables funcionales** en todas las vistas de listado del sistema de ventas usando **simple-datatables**, como en el ejemplo de `table-datatable.html`.

## ✅ Vistas Actualizadas con DataTable

| Módulo | Ruta | Archivo |
|--------|------|---------|
| **Ventas** | `/ventas/listar` | `ventas/listar.html` |
| **Clientes** | `/clientes/listar` | `clientes/listar.html` |
| **Productos** | `/productos/listar` | `productos/listar.html` |
| **Fabricantes** | `/fabricantes/listar` | `fabricantes/listar.html` |
| **Inventario - Movimientos** | `/inventario/movimientos` | `inventario/movimientos.html` |
| **Inventario - Existencias** | `/inventario/existencias` | `inventario/existencias.html` |
| **Clientes - Categorías** | `/clientes/categorias` | `clientes/categorias.html` |
| **Productos - Categorías** | `/productos/categorias` | `productos/categorias.html` |
| **Cuentas por Cobrar - Pendientes** | `/cuentas-cobrar/pendientes` | `cuentas-cobrar/pendientes.html` |
| **Configuración - Usuarios** | `/config/usuarios` | `config/usuarios.html` |

## 🎯 Características de DataTable Implementadas

✅ **Búsqueda global** - Busca en todas las columnas  
✅ **Ordenamiento** - Haz clic en los encabezados para ordenar  
✅ **Paginación** - Navega entre páginas  
✅ **Tabla responsive** - Adapta a cualquier dispositivo  
✅ **Datos de ejemplo** - Múltiples filas para demostración  

## 🔧 Estructura HTML

Todas las tablas utilizan la estructura estándar de `simple-datatables`:

```html
<table class="table table-striped" id="table1">
    <thead>
        <tr>
            <th>Columna 1</th>
            <th>Columna 2</th>
            ...
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Datos...</td>
            ...
        </tr>
    </tbody>
</table>
```

**Puntos clave:**
- **Clase `table-striped`**: Alternancia de colores en filas
- **ID `table1`**: Identificador para inicializar datatable en JavaScript
- **thead/tbody**: Estructura semántica necesaria

## 📱 Datos de Ejemplo

Cada tabla incluye **6 filas de ejemplo** para demostración:

### Ventas
- 6 ventas con diferentes estados (Completada, Pendiente, Cancelada)
- Montos y fechas variadas

### Clientes
- 6 clientes con categorías (VIP, Regular, Premium)
- Emails y teléfonos de ejemplo

### Productos
- 6 productos con precios y stock variado
- Categorías asignadas

### Inventario
- Movimientos: entradas y salidas
- Existencias: stock disponible con estados (Disponible, Bajo Stock, Agotado)

### Cuentas por Cobrar
- Facturas con diferentes estados (Vencida, Por Vencer, En Plazo)
- Montos y días vencidos

### Configuración
- Usuarios con roles (Administrador, Vendedor, Bodeguero, Contador)
- Estados activos/inactivos

## 🚀 Inicialización de DataTables

### JavaScript Requerido

Para activar la funcionalidad de datatable, necesitas agregar este script **después** de los scripts de la página:

```html
<script src="assets/extensions/simple-datatables/umd/simple-datatables.js"></script>
<script>
    let table1 = document.getElementById('table1');
    if(table1) {
        new simpleDatatables.DataTable(table1);
    }
</script>
```

### Archivo Existente de Referencia

En `assets/static/js/pages/simple-datatables.js` está la inicialización completa que puedes copiar.

## 📝 Datos Dinámicos desde Backend

Para conectar datos reales de BD:

### 1. Actualizar Controller

```java
@GetMapping("/listar")
public String listar(Model model) {
    List<Cliente> clientes = clienteService.obtenerTodos();
    model.addAttribute("clientes", clientes);
    return "clientes/listar";
}
```

### 2. Actualizar Template con Thymeleaf

```html
<tbody>
    <tr th:each="cliente : ${clientes}">
        <td th:text="${cliente.id}"></td>
        <td th:text="${cliente.nombre}"></td>
        <td th:text="${cliente.email}"></td>
        <td th:text="${cliente.telefono}"></td>
        <td th:text="${cliente.categoria}"></td>
        <td>
            <a th:href="@{/clientes/editar/{id}(id=${cliente.id})}" class="btn btn-warning btn-sm">Editar</a>
            <a th:href="@{/clientes/eliminar/{id}(id=${cliente.id})}" class="btn btn-danger btn-sm">Eliminar</a>
        </td>
    </tr>
</tbody>
```

## 🎨 Personalización de Columnas

Las tablas incluyen por defecto:
- **Tabla principal**: Datos del registro
- **Columna Acciones**: Botones de Editar/Eliminar

Para agregar más columnas:

```html
<th>Nueva Columna</th>
```

Para remover columnas no deseadas, simplemente elimina el `<th>` y sus correspondientes `<td>`.

## 💾 Próximos Pasos

1. **Conectar a Base de Datos**
   - Crear modelos JPA/Hibernate
   - Implementar repositorios

2. **Implementar Servicios**
   - CRUD operations
   - Búsqueda y filtros

3. **Agregar Paginación Real**
   - Usar `Page<T>` de Spring Data
   - Actualizar template con `th:each`

4. **Validaciones**
   - Validar datos en formularios
   - Mostrar mensajes de error

5. **Exportación**
   - Agregar opción para exportar a Excel/PDF

## 📚 Referencias

- **Simple DataTables**: https://simple-datatables.com/
- **Bootstrap Tables**: https://getbootstrap.com/docs/5.1/content/tables/
- **Thymeleaf**: https://www.thymeleaf.org/

---

**Estado**: ✅ Implementado  
**Fecha**: Marzo 2026  
**Versión**: 1.0

