package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.Cliente;
import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.entity.VentaDetalle;
import com.deyhayenterprise.mazeradmintemplate.repository.ClienteRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.VentaRepository;
import com.deyhayenterprise.mazeradmintemplate.service.VentaService;
import com.deyhayenterprise.mazeradmintemplate.web.form.VentaCreateForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.VentaDetalleForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return ventaRepository.findAllByOrderByFechaDescIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Venta findById(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada."));
    }

    @Override
    @Transactional
    public Venta create(VentaCreateForm form, String accion) {
        Cliente cliente = clienteRepository.findById(form.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setFecha(form.getFecha());
        venta.setEstado("COMPLETADA");

        applyDetalleAndStock(venta, form);
        return ventaRepository.save(venta);
    }

    @Override
    @Transactional
    public Venta update(Long id, VentaCreateForm form, String accion) {
        Venta venta = findById(id);
        Cliente cliente = clienteRepository.findById(form.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));

        // Reversar stock de detalle anterior
        for (VentaDetalle oldDetail : venta.getDetalles()) {
            Producto p = oldDetail.getProducto();
            p.setStock(p.getStock() + oldDetail.getCantidad());
            productoRepository.save(p);
        }

        venta.setCliente(cliente);
        venta.setFecha(form.getFecha());
        venta.getDetalles().clear();

        applyDetalleAndStock(venta, form);
        return ventaRepository.save(venta);
    }

    private void applyDetalleAndStock(Venta venta, VentaCreateForm form) {
        if (form.getDetalles() == null || form.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto al detalle.");
        }

        Map<Long, Integer> qtyByProduct = new LinkedHashMap<>();
        for (VentaDetalleForm d : form.getDetalles()) {
            if (d.getProductoId() == null || d.getCantidad() == null || d.getCantidad() < 1) {
                throw new IllegalArgumentException("Detalle de venta invalido.");
            }
            qtyByProduct.merge(d.getProductoId(), d.getCantidad(), Integer::sum);
        }

        Map<Long, Producto> products = new LinkedHashMap<>();
        for (Long productId : qtyByProduct.keySet()) {
            Producto producto = productoRepository.findByIdAndActivoTrue(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en el detalle."));
            Integer qty = qtyByProduct.get(productId);
            if (producto.getStock() < qty) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            products.put(productId, producto);
        }

        BigDecimal total = BigDecimal.ZERO;
        int totalCantidad = 0;
        Producto productoCabecera = null;

        for (Map.Entry<Long, Integer> e : qtyByProduct.entrySet()) {
            Producto producto = products.get(e.getKey());
            Integer qty = e.getValue();
            BigDecimal unit = producto.getPrecio();
            BigDecimal subtotal = unit.multiply(BigDecimal.valueOf(qty));

            if (productoCabecera == null) {
                productoCabecera = producto;
            }
            totalCantidad += qty;

            VentaDetalle det = new VentaDetalle();
            det.setVenta(venta);
            det.setProducto(producto);
            det.setCantidad(qty);
            det.setPrecioUnitario(unit);
            det.setSubtotal(subtotal);
            venta.getDetalles().add(det);

            total = total.add(subtotal);

            producto.setStock(producto.getStock() - qty);
            productoRepository.save(producto);
        }

        venta.setTotal(total);
        // Compatibilidad con esquema legacy de tabla ventas
        venta.setProducto(productoCabecera);
        venta.setCantidad(totalCantidad);
    }
}

