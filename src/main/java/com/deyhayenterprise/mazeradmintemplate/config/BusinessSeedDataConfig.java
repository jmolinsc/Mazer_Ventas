package com.deyhayenterprise.mazeradmintemplate.config;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.deyhayenterprise.mazeradmintemplate.entity.Cliente;
import com.deyhayenterprise.mazeradmintemplate.entity.ClienteCategoria;
import com.deyhayenterprise.mazeradmintemplate.entity.Fabricante;
import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.entity.ProductoCategoria;
import com.deyhayenterprise.mazeradmintemplate.entity.UnidadMedida;
import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.entity.VentaDetalle;
import com.deyhayenterprise.mazeradmintemplate.repository.ClienteCategoriaRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ClienteRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.FabricanteRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoCategoriaRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.UnidadMedidaRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.VentaRepository;

@Configuration
public class BusinessSeedDataConfig {

    @Bean
    CommandLineRunner seedBusinessData(ClienteRepository clienteRepository,
                                       ProductoRepository productoRepository,
                                       VentaRepository ventaRepository,
                                       FabricanteRepository fabricanteRepository,
                                       ClienteCategoriaRepository clienteCategoriaRepository,
                                       ProductoCategoriaRepository productoCategoriaRepository,
                                       UnidadMedidaRepository unidadMedidaRepository) {
        return args -> {
            if (unidadMedidaRepository.count() == 0) {
                unidadMedidaRepository.saveAll(List.of(
                        buildUnidad("Unidad", "Unidad base"),
                        buildUnidad("Caja", "Empaque en caja"),
                        buildUnidad("Paquete", "Empaque en paquete"),
                        buildUnidad("Kg", "Kilogramo"),
                        buildUnidad("Gramo", "Gramo"),
                        buildUnidad("Litro", "Litro"),
                        buildUnidad("Ml", "Mililitro"),
                        buildUnidad("Metro", "Metro lineal")
                ));
            }

            if (clienteCategoriaRepository.count() == 0) {
                clienteCategoriaRepository.saveAll(List.of(
                        buildClienteCategoria("VIP", "Clientes premium"),
                        buildClienteCategoria("Regular", "Clientes normales"),
                        buildClienteCategoria("Premium", "Clientes preferenciales"),
                        buildClienteCategoria("Mayorista", "Clientes al por mayor")
                ));
            }

            if (productoCategoriaRepository.count() == 0) {
                productoCategoriaRepository.saveAll(List.of(
                        buildProductoCategoria("Electronica", "Productos electronicos"),
                        buildProductoCategoria("Oficina", "Productos de oficina"),
                        buildProductoCategoria("Hogar", "Articulos para el hogar"),
                        buildProductoCategoria("Alimentos", "Productos de consumo")
                ));
            }

            // Clientes
            if (clienteRepository.count() == 0) {
                clienteRepository.saveAll(List.of(
                        buildCliente("Cliente A", "clientea@mazer.local", "555-1234", "VIP", "Av. Central 100"),
                        buildCliente("Cliente B", "clienteb@mazer.local", "555-5678", "Regular", "Calle 10 #23"),
                        buildCliente("Cliente C", "clientec@mazer.local", "555-9012", "Premium", "Zona Norte, local 4")
                ));
            }

            // Fabricantes
            if (fabricanteRepository.count() == 0) {
                fabricanteRepository.saveAll(List.of(
                        buildFabricante("Fabricante A", "fabricantea@mazer.local", "555-5678", "Colombia", "Av. Industrial 1"),
                        buildFabricante("Fabricante B", "fabricanteb@mazer.local", "555-8901", "Mexico",   "Zona Franca 22"),
                        buildFabricante("Fabricante C", "fabricantec@mazer.local", "555-2345", "Brasil",   "Parque Tecnologico 5"),
                        buildFabricante("Fabricante D", "fabricanted@mazer.local", "555-6789", "Espana",   "Polígono Sur 8"),
                        buildFabricante("Fabricante E", "fabricantee@mazer.local", "555-0123", "China",    "Export Zone 14"),
                        buildFabricante("Fabricante F", "fabricantef@mazer.local", "555-4567", "Japon",    "Shibuya Tech Park")
                ));
            }

            Fabricante fabricanteBase = fabricanteRepository.findAllByActivoTrueOrderByIdDesc().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No hay fabricantes activos para sembrar productos."));

            // Productos
            if (productoRepository.count() == 0) {
                productoRepository.saveAll(List.of(
                        buildProducto("P-001", "Laptop Pro 14",   "Electronica", new BigDecimal("1200.00"), 20, "Unidad", fabricanteBase),
                        buildProducto("P-002", "Impresora Laser",  "Oficina",    new BigDecimal("280.00"),  15, "Unidad", fabricanteBase),
                        buildProducto("P-003", "Silla Ergonomica", "Hogar",      new BigDecimal("190.00"),  30, "Unidad", fabricanteBase)
                ));
            }

            // Venta demo
            if (ventaRepository.count() == 0 && clienteRepository.count() > 0 && productoRepository.count() > 0) {
                Cliente cliente = clienteRepository.findAll().get(0);
                Producto producto = productoRepository.findAll().get(0);

                Venta venta = new Venta();
                venta.setCliente(cliente);
                venta.setProducto(producto);
                venta.setCantidad(1);
                venta.setFecha(LocalDate.now());
                venta.setEstado("COMPLETADA");

                VentaDetalle detalle = new VentaDetalle();
                detalle.setVenta(venta);
                detalle.setProducto(producto);
                detalle.setCantidad(1);
                detalle.setPrecioUnitario(producto.getPrecio());
                detalle.setSubtotal(producto.getPrecio());
                venta.getDetalles().add(detalle);
                venta.setTotal(producto.getPrecio());

                ventaRepository.save(venta);
                if (producto.getStock() > 0) {
                    producto.setStock(producto.getStock() - 1);
                    productoRepository.save(producto);
                }
            }
        };
    }

    private Cliente buildCliente(String nombre, String email, String telefono, String categoria, String direccion) {
        Cliente c = new Cliente();
        c.setNombre(nombre); c.setEmail(email); c.setTelefono(telefono);
        c.setCategoria(categoria); c.setDireccion(direccion); c.setActivo(true);
        return c;
    }

    private Producto buildProducto(String codigo, String nombre, String categoria, BigDecimal precio, int stock, String unidad, Fabricante fabricante) {
        Producto p = new Producto();
        p.setCodigo(codigo); p.setNombre(nombre); p.setCategoria(categoria);
        p.setPrecio(precio); p.setStock(stock); p.setUnidad(unidad); p.setActivo(true);
        p.setFabricante(fabricante);
        return p;
    }

    private Fabricante buildFabricante(String nombre, String email, String telefono, String pais, String direccion) {
        Fabricante f = new Fabricante();
        f.setNombre(nombre); f.setEmail(email); f.setTelefono(telefono);
        f.setPais(pais); f.setDireccion(direccion); f.setActivo(true);
        return f;
    }

    private ClienteCategoria buildClienteCategoria(String nombre, String descripcion) {
        ClienteCategoria c = new ClienteCategoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setActivo(true);
        return c;
    }

    private ProductoCategoria buildProductoCategoria(String nombre, String descripcion) {
        ProductoCategoria c = new ProductoCategoria();
        c.setNombre(nombre);
        c.setDescripcion(descripcion);
        c.setActivo(true);
        return c;
    }

    private UnidadMedida buildUnidad(String nombre, String descripcion) {
        UnidadMedida u = new UnidadMedida();
        u.setNombre(nombre);
        u.setDescripcion(descripcion);
        u.setActivo(true);
        return u;
    }
}
