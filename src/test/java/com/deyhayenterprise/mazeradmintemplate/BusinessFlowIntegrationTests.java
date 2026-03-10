package com.deyhayenterprise.mazeradmintemplate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.deyhayenterprise.mazeradmintemplate.entity.Cliente;
import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.repository.ClienteRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BusinessFlowIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Test
    void adminShouldCreateClienteProductoAndVenta() throws Exception {
        MockHttpSession session = login("admin", "Admin123*");
        String suffix = String.valueOf(System.currentTimeMillis());

        String email = "cliente." + suffix + "@mazer.local";
        String codigo = "PRD_" + suffix;

        mockMvc.perform(post("/clientes/nuevo").session(session)
                        .param("nombre", "Cliente " + suffix)
                        .param("email", email)
                        .param("telefono", "555-0000")
                        .param("categoria", "VIP")
                        .param("direccion", "Direccion test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes/listar"));

        mockMvc.perform(post("/productos/nuevo").session(session)
                        .param("nombre", "Producto " + suffix)
                        .param("codigo", codigo)
                        .param("categoria", "Oficina")
                        .param("precio", "99.90")
                        .param("stock", "12")
                        .param("unidad", "Unidad"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos/listar"));

        Cliente cliente = clienteRepository.findByEmailIgnoreCase(email).orElseThrow();
        Producto producto = productoRepository.findByCodigoIgnoreCase(codigo).orElseThrow();

        mockMvc.perform(post("/ventas/nueva").session(session)
                        .param("clienteId", String.valueOf(cliente.getId()))
                        .param("productoId", String.valueOf(producto.getId()))
                        .param("cantidad", "2")
                        .param("fecha", "2026-03-10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/ventas/listar"));

        mockMvc.perform(get("/ventas/listar").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Cliente " + suffix)))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Producto " + suffix)));
    }

    @Test
    void shouldRejectDuplicateClienteEmail() throws Exception {
        MockHttpSession session = login("admin", "Admin123*");

        mockMvc.perform(post("/clientes/nuevo").session(session)
                        .param("nombre", "Cliente duplicado")
                        .param("email", "clientea@mazer.local")
                        .param("telefono", "555-1111")
                        .param("categoria", "Regular")
                        .param("direccion", "Direccion"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ya existe un cliente con ese correo.")));
    }

    @Test
    void shouldRejectVentaWithInsufficientStock() throws Exception {
        MockHttpSession session = login("admin", "Admin123*");
        String suffix = "LOW_" + System.currentTimeMillis();

        mockMvc.perform(post("/productos/nuevo").session(session)
                        .param("nombre", "Stock corto")
                        .param("codigo", suffix)
                        .param("categoria", "Hogar")
                        .param("precio", "10.00")
                        .param("stock", "1")
                        .param("unidad", "Unidad"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos/listar"));

        Producto producto = productoRepository.findByCodigoIgnoreCase(suffix).orElseThrow();
        Cliente cliente = clienteRepository.findByEmailIgnoreCase("clientea@mazer.local").orElseThrow();

        mockMvc.perform(post("/ventas/nueva").session(session)
                        .param("clienteId", String.valueOf(cliente.getId()))
                        .param("productoId", String.valueOf(producto.getId()))
                        .param("cantidad", "5")
                        .param("fecha", "2026-03-10"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Stock insuficiente para registrar la venta.")));
    }

    @Test
    void shouldUpdateClienteAndProducto() throws Exception {
        MockHttpSession session = login("admin", "Admin123*");

        Cliente cliente = clienteRepository.findByEmailIgnoreCase("clientea@mazer.local").orElseThrow();
        Producto producto = productoRepository.findByCodigoIgnoreCase("P-001").orElseThrow();

        mockMvc.perform(post("/clientes/editar/" + cliente.getId()).session(session)
                        .param("nombre", "Cliente A Editado")
                        .param("email", "clientea@mazer.local")
                        .param("telefono", "555-9999")
                        .param("categoria", "Premium")
                        .param("direccion", "Nueva direccion"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes/listar"));

        mockMvc.perform(post("/productos/editar/" + producto.getId()).session(session)
                        .param("nombre", "Laptop Pro 14 Plus")
                        .param("codigo", "P-001")
                        .param("categoria", "Electronica")
                        .param("precio", "1300.00")
                        .param("stock", "18")
                        .param("unidad", "Unidad"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos/listar"));

        mockMvc.perform(get("/clientes/listar").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Cliente A Editado")));

        mockMvc.perform(get("/productos/listar").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Laptop Pro 14 Plus")));
    }

    @Test
    void shouldSoftDeleteClienteAndProducto() throws Exception {
        MockHttpSession session = login("admin", "Admin123*");
        String suffix = String.valueOf(System.currentTimeMillis());

        String email = "delete." + suffix + "@mazer.local";
        String codigo = "DEL_" + suffix;

        mockMvc.perform(post("/clientes/nuevo").session(session)
                        .param("nombre", "Cliente Delete " + suffix)
                        .param("email", email)
                        .param("telefono", "555-0000")
                        .param("categoria", "VIP")
                        .param("direccion", "Direccion test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes/listar"));

        mockMvc.perform(post("/productos/nuevo").session(session)
                        .param("nombre", "Producto Delete " + suffix)
                        .param("codigo", codigo)
                        .param("categoria", "Oficina")
                        .param("precio", "50.00")
                        .param("stock", "5")
                        .param("unidad", "Unidad"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos/listar"));

        Cliente cliente = clienteRepository.findByEmailIgnoreCase(email).orElseThrow();
        Producto producto = productoRepository.findByCodigoIgnoreCase(codigo).orElseThrow();

        mockMvc.perform(post("/clientes/eliminar/" + cliente.getId()).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clientes/listar"));

        mockMvc.perform(post("/productos/eliminar/" + producto.getId()).session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/productos/listar"));

        mockMvc.perform(get("/clientes/listar").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Cliente Delete " + suffix))));

        mockMvc.perform(get("/productos/listar").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Producto Delete " + suffix))));
    }

    private MockHttpSession login(String username, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth-login")
                        .param("username", username)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"))
                .andReturn();

        return (MockHttpSession) result.getRequest().getSession(false);
    }
}
