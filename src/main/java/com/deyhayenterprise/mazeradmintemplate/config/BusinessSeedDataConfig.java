package com.deyhayenterprise.mazeradmintemplate.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.deyhayenterprise.mazeradmintemplate.entity.Cliente;
import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.repository.ClienteRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.VentaRepository;

@Configuration
public class BusinessSeedDataConfig {

    @Bean
    CommandLineRunner seedBusinessData(ClienteRepository clienteRepository,
                                       ProductoRepository productoRepository,
                                       VentaRepository ventaRepository) {
        return args -> {
            if (clienteRepository.count() == 0) {
                clienteRepository.saveAll(List.of(
                        buildCliente("Cliente A", "clientea@mazer.local", "555-1234", "VIP", "Av. Central 100"),
                        buildCliente("Cliente B", "clienteb@mazer.local", "555-5678", "Regular", "Calle 10 #23"),
                        buildCliente("Cliente C", "clientec@mazer.local", "555-9012", "Premium", "Zona Norte, local 4")
                ));
            }

            if (productoRepository.count() == 0) {
                productoRepository.saveAll(List.of(
                        buildProducto("P-001", "Laptop Pro 14", "Electrónica", new BigDecimal("1200.00"), 20, "Unidad"),
                        buildProducto("P-002", "Impresora Laser", "Oficina", new BigDecimal("280.00"), 15, "Unidad"),
                        buildProducto("P-003", "Silla Ergonomica", "Hogar", new BigDecimal("190.00"), 30, "Unidad")
                ));
            }

            if (ventaRepository.count() == 0 && clienteRepository.count() > 0 && productoRepository.count() > 0) {
                Cliente cliente = clienteRepository.findAll().get(0);
                Producto producto = productoRepository.findAll().get(0);

                Venta venta = new Venta();
                venta.setCliente(cliente);
                venta.setProducto(producto);
                venta.setCantidad(1);
                venta.setFecha(LocalDate.now());
                venta.setTotal(producto.getPrecio());
                venta.setEstado("COMPLETADA");
                ventaRepository.save(venta);

                if (producto.getStock() > 0) {
                    producto.setStock(producto.getStock() - 1);
                    productoRepository.save(producto);
                }
            }
        };
    }

    private Cliente buildCliente(String nombre, String email, String telefono, String categoria, String direccion) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setEmail(email);
        cliente.setTelefono(telefono);
        cliente.setCategoria(categoria);
        cliente.setDireccion(direccion);
        cliente.setActivo(true);
        return cliente;
    }

    private Producto buildProducto(String codigo, String nombre, String categoria, BigDecimal precio, int stock, String unidad) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setUnidad(unidad);
        producto.setActivo(true);
        return producto;
    }
}
