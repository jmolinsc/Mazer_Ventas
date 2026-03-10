// Inicialización de Simple DataTables para todas las páginas

document.addEventListener('DOMContentLoaded', function() {
    // Inicializar datatable si existe la tabla con id "table1"
    let table1 = document.getElementById('table1');
    if(table1) {
        new simpleDatatables.DataTable(table1, {
            perPage: 10,  // Filas por página
            searchable: true,  // Habilitar búsqueda
            sortable: true,  // Habilitar ordenamiento
            labels: {
                placeholder: "Buscar...",
                perPage: "registros por página",
                noRows: "No hay registros",
                info: "Mostrando {start} a {end} de {rows} registros",
            }
        });
    }

    // Puedes agregar más tablas si es necesario:
    // let table2 = document.getElementById('table2');
    // if(table2) {
    //     new simpleDatatables.DataTable(table2);
    // }
});

