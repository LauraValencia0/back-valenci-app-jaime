package com.valenci.controladores;

import com.valenci.config.JwtAuthenticationFilter;
import com.valenci.config.SecurityConfig;
import com.valenci.entidades.Cliente;
import com.valenci.entidades.Proveedor;
import com.valenci.entidades.Usuario;
import com.valenci.servicios.JwtService;
import com.valenci.servicios.ServicioUsuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ControladorAdmin.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
        })
@Import(SecurityConfig.class)
@DisplayName("Pruebas para ControladorAdmin")
class ControladorAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicioUsuario servicioUsuario;

    @MockBean
    private JwtService jwtService; // Mock necesario por la configuración de seguridad


    @Test
    @DisplayName("Debe devolver todos los usuarios cuando el usuario es ADMIN")
    @WithMockUser(roles = "ADMINISTRADOR")
    void obtenerTodosLosUsuarios_cuandoEsAdmin_debeDevolverListaDeUsuarios() throws Exception {
        // --- ARRANGE (CORREGIDO) ---
        // Se crean los objetos usando el constructor vacío y los setters.
        // Esto es más robusto y se alinea con las entidades refactorizadas.

        Cliente cliente = new Cliente();
        cliente.setId(1);
        cliente.setNombre("Cliente Test");
        cliente.setCorreo("cliente@test.com");
        cliente.setDireccionEnvio("dir");
        cliente.setRol("CLIENTE");

        Proveedor proveedor = new Proveedor();
        proveedor.setId(2);
        proveedor.setNombre("Proveedor Test");
        proveedor.setCorreo("prov@test.com");
        proveedor.setNombreEmpresa("empresa");
        proveedor.setRol("PROVEEDOR");

        List<Usuario> listaUsuarios = Arrays.asList(cliente, proveedor);

        when(servicioUsuario.listarTodos()).thenReturn(listaUsuarios);

        // Act & Assert
        mockMvc.perform(get("/api/admin/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Cliente Test"))
                .andExpect(jsonPath("$[0].rol").value("CLIENTE"))
                .andExpect(jsonPath("$[1].nombre").value("Proveedor Test"))
                .andExpect(jsonPath("$[1].rol").value("PROVEEDOR"));
    }

    @Test
    @DisplayName("Debe devolver una lista vacía cuando no hay usuarios")
    @WithMockUser(roles = "ADMINISTRADOR") // Corregido a "ADMINISTRADOR" por consistencia
    void obtenerTodosLosUsuarios_cuandoNoHayUsuarios_debeDevolverListaVacia() throws Exception {
        // Arrange
        when(servicioUsuario.listarTodos()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/admin/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }


    @Test
    @DisplayName("Debe denegar el acceso cuando el usuario no es ADMIN")
    @WithMockUser(roles = "CLIENTE")
    void obtenerTodosLosUsuarios_cuandoNoEsAdmin_debeDevolverForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Debe denegar el acceso cuando no hay usuario autenticado")
    void obtenerTodosLosUsuarios_cuandoNoAutenticado_debeDevolverForbidden() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/admin/usuarios"))
                .andExpect(status().isForbidden());
    }
}
