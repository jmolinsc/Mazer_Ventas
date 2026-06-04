function actualizarComportamientoInventario(selectElement) {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    const comportamiento = selectedOption.getAttribute("data-comportamiento") || "";

    const comportamientoInput = document.getElementById("comportamiento");
    if (comportamientoInput) {
        comportamientoInput.value = comportamiento;
    }
}

document.addEventListener("DOMContentLoaded", function () {
    const movSelect = document.getElementById("mov");
    if (movSelect && movSelect.value) {
        actualizarComportamientoInventario(movSelect);
    }
});

