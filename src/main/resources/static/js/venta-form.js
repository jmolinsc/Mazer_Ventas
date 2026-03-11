(function () {
    const detalleBody = document.getElementById("detalleBody");
    const detalleInputs = document.getElementById("detalleInputs");
    const footerCantidad = document.getElementById("footerCantidad");
    const footerTotal = document.getElementById("footerTotal");

    if (!detalleBody || !detalleInputs) {
        return;
    }

    const detalleMap = new Map();

    function formatMoney(value) {
        return Number(value).toFixed(2);
    }

    function findMeta(productId) {
        const btn = document.querySelector(`.btn-agregar-producto[data-id="${productId}"]`);
        if (!btn) {
            return { nombre: "Producto", precio: 0, stock: 0 };
        }
        return {
            nombre: btn.getAttribute("data-nombre") || "Producto",
            precio: Number(btn.getAttribute("data-precio") || "0"),
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
                    precio: meta.precio,
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
        let total = 0;

        for (const item of detalleMap.values()) {
            const subtotal = item.precio * item.cantidad;
            totalQty += item.cantidad;
            total += subtotal;

            const tr = document.createElement("tr");
            tr.innerHTML =
                `<td>${item.nombre}</td>
                 <td>${formatMoney(item.precio)}</td>
                 <td>${item.cantidad}</td>
                 <td>${formatMoney(subtotal)}</td>
                 <td><button type="button" class="btn btn-danger btn-sm">Quitar</button></td>`;

            tr.querySelector("button").addEventListener("click", function () {
                detalleMap.delete(item.productoId);
                render();
            });

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
        footerTotal.textContent = formatMoney(total);
    }

    document.querySelectorAll(".btn-agregar-producto").forEach(function (btn) {
        btn.addEventListener("click", function () {
            const productId = Number(btn.getAttribute("data-id"));
            const nombre = btn.getAttribute("data-nombre") || "Producto";
            const precio = Number(btn.getAttribute("data-precio") || "0");
            const stock = Number(btn.getAttribute("data-stock") || "0");
            const inputCantidad = btn.closest("tr").querySelector(".modal-cantidad");
            const cantidad = Number(inputCantidad && inputCantidad.value ? inputCantidad.value : "1");

            if (cantidad < 1) {
                alert("La cantidad debe ser mayor a 0.");
                return;
            }
            if (cantidad > stock) {
                alert("La cantidad excede la existencia en stock.");
                return;
            }

            const current = detalleMap.get(productId);
            const nuevaCantidad = (current ? current.cantidad : 0) + cantidad;

            if (nuevaCantidad > stock) {
                alert("La cantidad total para este producto excede la existencia en stock.");
                return;
            }

            detalleMap.set(productId, {
                productoId: productId,
                nombre: nombre,
                precio: precio,
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
