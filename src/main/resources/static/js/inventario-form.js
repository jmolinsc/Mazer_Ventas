(function () {
    const detalleBody = document.getElementById("detalleBody");
    const detalleInputs = document.getElementById("detalleInputs");
    const footerCantidad = document.getElementById("footerCantidad");
    const isReadOnly = (document.getElementById("isReadOnlyFlag")?.value || "false") === "true";

    if (!detalleBody || !detalleInputs) {
        return;
    }

    const detalleMap = new Map();

    function findMeta(productId) {
        const btn = document.querySelector(`.btn-agregar-producto[data-id="${productId}"]`);
        if (!btn) {
            return { nombre: "Producto", unidad: "", stock: 0 };
        }
        return {
            nombre: btn.getAttribute("data-nombre") || "Producto",
            unidad: btn.getAttribute("data-unidad") || "",
            stock: Number(btn.getAttribute("data-stock") || "0")
        };
    }

    function bootstrapFromHiddenInputs() {
        const productInputs = detalleInputs.querySelectorAll('input[name$=".productoId"]');
        for (const pInput of productInputs) {
            const name = pInput.getAttribute("name") || "";
            const idxStart = name.indexOf("[") + 1;
            const idxEnd = name.indexOf("]");
            const idx = name.substring(idxStart, idxEnd);
            const qInput = detalleInputs.querySelector(`input[name='detalles[${idx}].cantidad']`);
            const productId = Number(pInput.value || "0");
            const cantidad = Number(qInput ? qInput.value : "0");
            if (productId > 0 && cantidad > 0) {
                const meta = findMeta(productId);
                detalleMap.set(productId, {
                    productoId: productId,
                    nombre: meta.nombre,
                    unidad: meta.unidad,
                    stock: meta.stock,
                    cantidad: cantidad
                });
            }
        }
    }

    function render() {
        detalleBody.innerHTML = "";
        detalleInputs.innerHTML = "";

        let index = 0;
        let totalQty = 0;

        for (const item of detalleMap.values()) {
            totalQty += item.cantidad;

            const tr = document.createElement("tr");
            tr.innerHTML =
                `<td>${item.nombre}</td>
                 <td>${item.unidad}</td>
                 <td>${item.stock}</td>
                 <td>${item.cantidad}</td>
                 <td><button type="button" class="btn btn-danger btn-sm" ${isReadOnly ? "disabled" : ""}>Quitar</button></td>`;

            if (!isReadOnly) {
                tr.querySelector("button").addEventListener("click", function () {
                    detalleMap.delete(item.productoId);
                    render();
                });
            }

            detalleBody.appendChild(tr);

            const inputProducto = document.createElement("input");
            inputProducto.type = "hidden";
            inputProducto.name = `detalles[${index}].productoId`;
            inputProducto.value = String(item.productoId);

            const inputCantidad = document.createElement("input");
            inputCantidad.type = "hidden";
            inputCantidad.name = `detalles[${index}].cantidad`;
            inputCantidad.value = String(item.cantidad);

            detalleInputs.appendChild(inputProducto);
            detalleInputs.appendChild(inputCantidad);

            index += 1;
        }

        footerCantidad.textContent = String(totalQty);
    }

    document.querySelectorAll(".btn-agregar-producto").forEach(function (btn) {
        btn.addEventListener("click", function () {
            const productId = Number(btn.getAttribute("data-id"));
            const nombre = btn.getAttribute("data-nombre") || "Producto";
            const unidad = btn.getAttribute("data-unidad") || "";
            const stock = Number(btn.getAttribute("data-stock") || "0");
            const inputCantidad = btn.closest("tr").querySelector(".modal-cantidad");
            const cantidad = Number(inputCantidad && inputCantidad.value ? inputCantidad.value : "1");

            if (cantidad < 1) {
                alert("La cantidad debe ser mayor a 0.");
                return;
            }

            const current = detalleMap.get(productId);
            const nuevaCantidad = (current ? current.cantidad : 0) + cantidad;

            detalleMap.set(productId, {
                productoId: productId,
                nombre: nombre,
                unidad: unidad,
                stock: stock,
                cantidad: nuevaCantidad
            });

            render();
        });
    });

    document.getElementById("btnLimpiar")?.addEventListener("click", function () {
        detalleMap.clear();
        render();
    });

    bootstrapFromHiddenInputs();
    render();
})();

