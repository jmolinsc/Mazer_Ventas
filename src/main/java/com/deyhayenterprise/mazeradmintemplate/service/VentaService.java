package com.deyhayenterprise.mazeradmintemplate.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.Cliente;
import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.repository.ClienteRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.VentaRepository;
import com.deyhayenterprise.mazeradmintemplate.web.form.VentaCreateForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;

    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return ventaRepository.findAllByOrderByFechaDescIdDesc();
    }

    @Transactional
    public Venta create(VentaCreateForm form) {
        Cliente cliente = clienteRepository.findById(form.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
        Producto producto = productoRepository.findById(form.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        if (producto.getStock() < form.getCantidad()) {
            throw new IllegalArgumentException("Stock insuficiente para registrar la venta.");
        }

        BigDecimal total = producto.getPrecio().multiply(BigDecimal.valueOf(form.getCantidad()));

        Venta venta = new Venta();
        venta.setCliente(cliente);
        venta.setProducto(producto);
        venta.setCantidad(form.getCantidad());
        venta.setFecha(form.getFecha());
        venta.setTotal(total);
        venta.setEstado("COMPLETADA");

        producto.setStock(producto.getStock() - form.getCantidad());
        productoRepository.save(producto);

        return ventaRepository.save(venta);
    }
}

