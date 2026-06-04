(function () {
    const select = document.querySelector("select[data-choices-movtipos='true']");
    if (!select || typeof Choices === "undefined") {
        return;
    }

    // UI tipo tags/chips para selección múltiple de movtipos.
    new Choices(select, {
        removeItemButton: true,
        searchEnabled: true,
        searchResultLimit: 50,
        shouldSort: false,
        itemSelectText: "",
        placeholder: true,
        placeholderValue: "Selecciona movtipos...",
        noResultsText: "Sin resultados",
        noChoicesText: "Sin opciones disponibles"
    });
})();

