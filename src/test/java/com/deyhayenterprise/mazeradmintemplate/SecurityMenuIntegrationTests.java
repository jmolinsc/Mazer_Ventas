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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityMenuIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginPageShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/auth-login"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Usuarios demo sembrados automáticamente")));
    }

    @Test
    void adminShouldLoginAndAccessConfigUsers() throws Exception {
        MockHttpSession session = login("admin", "Admin123*");

        mockMvc.perform(get("/config/usuarios").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Usuarios del sistema")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nuevo usuario")));
    }

    @Test
    void vendedorShouldSeeSalesButNotConfiguration() throws Exception {
        MockHttpSession session = login("vendedor", "Ventas123*");

        mockMvc.perform(get("/ventas/nueva").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Nueva Venta")));

        mockMvc.perform(get("/config/usuarios").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void inactiveUserShouldNotLogin() throws Exception {
        mockMvc.perform(post("/auth-login")
                        .param("username", "contador")
                        .param("password", "Conta123*"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth-login?error=true"));
    }

    @Test
    void adminShouldCreateRoleFromPermissionsScreen() throws Exception {
        MockHttpSession session = login("admin", "Admin123*");

        mockMvc.perform(post("/config/permisos/roles").session(session)
                        .param("codigo", "SUPERVISOR_TEST")
                        .param("nombre", "Supervisor Test")
                        .param("descripcion", "Rol creado desde prueba"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/config/permisos"));

        mockMvc.perform(get("/config/permisos").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Supervisor Test")));
    }

    @Test
    void shouldRejectDuplicateRoleCode() throws Exception {
        MockHttpSession session = login("admin", "Admin123*");

        mockMvc.perform(post("/config/permisos/roles").session(session)
                        .param("codigo", "ADMIN")
                        .param("nombre", "Administrador Clonado")
                        .param("descripcion", "No debe crearse"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/config/permisos"));

        mockMvc.perform(get("/config/permisos").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Ya existe un rol con ese código.")));
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

