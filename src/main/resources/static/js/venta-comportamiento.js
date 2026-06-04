/**
 * Actualiza automáticamente el campo comportamiento cuando se selecciona un movtipo
 */
function actualizarComportamiento(selectElement) {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    const comportamiento = selectedOption.getAttribute('data-comportamiento') || '';

    const comportamientoInput = document.getElementById('comportamiento');
    if (comportamientoInput) {
        comportamientoInput.value = comportamiento;
        console.log('Comportamiento actualizado:', comportamiento);
    }
}

// Ejecutar al cargar la página para pre-poblar si ya hay valor seleccionado
document.addEventListener('DOMContentLoaded', function() {
    const movSelect = document.getElementById('mov');
    if (movSelect && movSelect.value) {
        actualizarComportamiento(movSelect);
    }
});

