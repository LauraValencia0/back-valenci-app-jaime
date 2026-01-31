package com.valenci.servicios;

import com.valenci.entidades.Cliente;
import com.valenci.entidades.Usuario;
import com.valenci.repositorios.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias para la clase ServicioUsuarioImpl.
 * Se utiliza Mockito para simular las dependencias (Repositorio y PasswordEncoder)
 * y probar la lógica de negocio de forma aislada.
 */
@ExtendWith(MockitoExtension.class)
class ServicioUsuarioImplTest {

    // @Mock crea una simulación (un "doble de prueba") de estas dependencias.
    // No se usarán las implementaciones reales.
    @Mock
    private RepositorioUsuario repositorioUsuario;

    @Mock
    private PasswordEncoder codificadorDeContrasena;

    // @InjectMocks crea una instancia de ServicioUsuarioImpl e intenta inyectar
    // los mocks definidos arriba (@Mock) en ella.
    @InjectMocks
    private ServicioUsuarioImpl servicioUsuario;

    private Usuario usuarioDePrueba;
    private Usuario datosParaActualizar;


    @BeforeEach
    void setUp() {
        // Creamos un objeto de prueba que usaremos en varios tests
        usuarioDePrueba = new Cliente();
        usuarioDePrueba.setId(1);
        usuarioDePrueba.setNombre("Juan Perez");
        usuarioDePrueba.setCorreo("juan.perez@example.com");
        usuarioDePrueba.setContrasena("contrasenaPlana");

        // Creamos un objeto con los datos nuevos para las pruebas de actualización
        datosParaActualizar = new Cliente();
        datosParaActualizar.setId(1);
        datosParaActualizar.setNombre("Juan Perez Actualizado");
        datosParaActualizar.setCorreo("juan.perez.nuevo@example.com");
    }

    @Test
    void registrar_cuandoCorreoNoExiste_debeGuardarUsuario() {
        // --- PREPARACIÓN (Arrange) ---
        when(repositorioUsuario.findByCorreo(anyString())).thenReturn(Optional.empty());
        when(codificadorDeContrasena.encode(anyString())).thenReturn("contrasenaHasheada");
        when(repositorioUsuario.save(any(Usuario.class))).thenReturn(usuarioDePrueba);


        // --- EJECUCIÓN (Act) ---
        servicioUsuario.registrar(usuarioDePrueba);


        // --- VERIFICACIÓN (Assert) ---
        verify(repositorioUsuario, times(1)).save(any(Usuario.class));
        verify(codificadorDeContrasena, times(1)).encode("contrasenaPlana");
    }

    @Test
    void registrar_cuandoCorreoYaExiste_debeLanzarExcepcion() {
        // --- PREPARACIÓN (Arrange) ---
        when(repositorioUsuario.findByCorreo(usuarioDePrueba.getCorreo())).thenReturn(Optional.of(usuarioDePrueba));

        // --- EJECUCIÓN Y VERIFICACIÓN (Act & Assert) ---
        assertThrows(IllegalArgumentException.class, () -> {
            servicioUsuario.registrar(usuarioDePrueba);
        });

        verify(repositorioUsuario, never()).save(any(Usuario.class));
    }

    @Test
    void buscarPorId_cuandoUsuarioExiste_debeDevolverUsuario() {
        // --- PREPARACIÓN (Arrange) ---
        when(repositorioUsuario.findById(1)).thenReturn(Optional.of(usuarioDePrueba));

        // --- EJECUCIÓN (Act) ---
        Optional<Usuario> resultado = servicioUsuario.buscarPorId(1);

        // --- VERIFICACIÓN (Assert) ---
        assertTrue(resultado.isPresent(), "El Optional no debería estar vacío");
        assertEquals("Juan Perez", resultado.get().getNombre());
    }

    @Test
    void buscarPorId_cuandoUsuarioNoExiste_debeDevolverOptionalVacio() {
        // --- PREPARACIÓN (Arrange) ---
        when(repositorioUsuario.findById(99)).thenReturn(Optional.empty());

        // --- EJECUCIÓN (Act) ---
        Optional<Usuario> resultado = servicioUsuario.buscarPorId(99);

        // --- VERIFICACIÓN (Assert) ---
        assertFalse(resultado.isPresent(), "El Optional debería estar vacío");
    }

    @Test
    void actualizar_cuandoUsuarioExiste_debeGuardarCambios() {
        // --- PREPARACIÓN (Arrange) ---
        // Cuando se busque el usuario original, lo devolveremos.
        when(repositorioUsuario.findById(1)).thenReturn(Optional.of(usuarioDePrueba));
        // Cuando se guarde, simplemente lo aceptamos (no necesitamos devolver nada).
        when(repositorioUsuario.save(any(Usuario.class))).thenReturn(datosParaActualizar);

        // --- EJECUCIÓN (Act) ---
        servicioUsuario.actualizar(datosParaActualizar);

        // --- VERIFICACIÓN (Assert) ---
        // Usamos un ArgumentCaptor para "capturar" el objeto que se pasa al método save.
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(repositorioUsuario).save(usuarioCaptor.capture());

        // Obtenemos el usuario capturado.
        Usuario usuarioGuardado = usuarioCaptor.getValue();

        // Verificamos que los datos del objeto guardado son los correctos.
        assertEquals("Juan Perez Actualizado", usuarioGuardado.getNombre());
        assertEquals("juan.perez.nuevo@example.com", usuarioGuardado.getCorreo());
    }

    @Test
    void actualizar_cuandoUsuarioNoExiste_debeLanzarExcepcion() {
        // --- PREPARACIÓN (Arrange) ---
        // Simulamos que el usuario no se encuentra en la base de datos.
        when(repositorioUsuario.findById(anyInt())).thenReturn(Optional.empty());

        // --- EJECUCIÓN Y VERIFICACIÓN (Act & Assert) ---
        assertThrows(IllegalArgumentException.class, () -> {
            servicioUsuario.actualizar(datosParaActualizar);
        });

        // Verificamos que NUNCA se intentó guardar nada.
        verify(repositorioUsuario, never()).save(any(Usuario.class));
    }

    @Test
    void eliminarPorId_cuandoUsuarioExiste_debeLlamarDeleteById() {
        // --- PREPARACIÓN (Arrange) ---
        // Simulamos que el usuario sí existe.
        when(repositorioUsuario.existsById(1)).thenReturn(true);
        // Le decimos a Mockito que no haga nada cuando se llame a deleteById (comportamiento por defecto para métodos void).
        doNothing().when(repositorioUsuario).deleteById(1);

        // --- EJECUCIÓN (Act) ---
        servicioUsuario.eliminarPorId(1);

        // --- VERIFICACIÓN (Assert) ---
        // Verificamos que el método deleteById fue llamado exactamente una vez con el ID correcto.
        verify(repositorioUsuario, times(1)).deleteById(1);
    }
}

