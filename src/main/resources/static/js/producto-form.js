(function () {
    function updatePreview(fileInput) {
        const preview = document.getElementById("previewImagenProducto");
        if (!preview || !fileInput || !fileInput.files || fileInput.files.length === 0) {
            return;
        }
        const file = fileInput.files[0];
        const objectUrl = URL.createObjectURL(file);
        preview.src = objectUrl;
    }

    // Expuesto para usarlo desde onchange del input, por si el listener no engancha a tiempo.
    window.previewImagenProductoFile = function (event) {
        updatePreview(event && event.target ? event.target : document.getElementById("imagenFile"));
    };

    document.addEventListener("change", function (event) {
        if (event.target && event.target.id === "imagenFile") {
            updatePreview(event.target);
        }
    });
})();

