package com.valenci.servicios;

import com.valenci.entidades.Producto;
import com.valenci.repositorios.RepositorioProducto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioProductoImplTest {

    @Mock
    private RepositorioProducto repositorioProducto;

    @InjectMocks
    private ServicioProductoImpl servicioProducto;

    private Producto producto;

    @BeforeEach
    void setUp() {
        // Preparamos un objeto Producto de prueba que usaremos en varios tests
        producto = new Producto();
        producto.setIdProducto(1);
        producto.setNombreProducto("Café de Origen");
        producto.setDescripcion("Café de las montañas de Antioquia");
        producto.setPrecio(new BigDecimal("35000.00"));
        producto.setCantidad(100);
    }

    @Test
    void crear_cuandoProductoEsValido_debeGuardarYDevolverProducto() {
        // Arrange (Preparación)
        when(repositorioProducto.save(any(Producto.class))).thenReturn(producto);

        // Act (Ejecución)
        Producto productoGuardado = servicioProducto.crear(producto);

        // Assert (Verificación)
        assertNotNull(productoGuardado);
        assertEquals("Café de Origen", productoGuardado.getNombreProducto());
        verify(repositorioProducto, times(1)).save(producto); // Verificamos que se llamó al método save una vez
    }

    @Test
    void crear_cuandoProductoEsNulo_debeLanzarExcepcion() {
        // Arrange, Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.crear(null);
        });
        verify(repositorioProducto, never()).save(any()); // Verificamos que NUNCA se llamó a save
    }

    @Test
    void actualizar_cuandoProductoExiste_debeActualizarYDevolverProducto() {
        // Arrange
        Producto datosNuevos = new Producto();
        datosNuevos.setNombreProducto("Café Premium");
        datosNuevos.setPrecio(new BigDecimal("45000.00"));

        when(repositorioProducto.findById(1)).thenReturn(Optional.of(producto));
        when(repositorioProducto.save(any(Producto.class))).thenReturn(producto);

        // Act
        Producto productoActualizado = servicioProducto.actualizar(1, datosNuevos);

        // Assert
        assertNotNull(productoActualizado);
        assertEquals("Café Premium", productoActualizado.getNombreProducto());
        assertEquals(new BigDecimal("45000.00"), productoActualizado.getPrecio());
        verify(repositorioProducto, times(1)).findById(1);
        verify(repositorioProducto, times(1)).save(producto);
    }

    @Test
    void actualizar_cuandoProductoNoExiste_debeLanzarExcepcion() {
        // Arrange
        when(repositorioProducto.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.actualizar(99, new Producto());
        });
        verify(repositorioProducto, never()).save(any());
    }

    @Test
    void eliminarPorId_cuandoProductoExiste_debeLlamarDeleteById() {
        // Arrange
        when(repositorioProducto.existsById(1)).thenReturn(true);
        // doNothing() es útil para métodos que devuelven void
        doNothing().when(repositorioProducto).deleteById(1);

        // Act
        servicioProducto.eliminarPorId(1);

        // Assert
        verify(repositorioProducto, times(1)).existsById(1);
        verify(repositorioProducto, times(1)).deleteById(1);
    }

    @Test
    void eliminarPorId_cuandoProductoNoExiste_debeLanzarExcepcion() {
        // Arrange
        when(repositorioProducto.existsById(99)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicioProducto.eliminarPorId(99);
        });
        verify(repositorioProducto, never()).deleteById(anyInt());
    }

    @Test
    void buscarPorId_cuandoProductoExiste_debeDevolverOptionalConProducto() {
        // Arrange
        when(repositorioProducto.findById(1)).thenReturn(Optional.of(producto));

        // Act
        Optional<Producto> resultado = servicioProducto.buscarPorId(1);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(producto.getIdProducto(), resultado.get().getIdProducto());
    }

    @Test
    void listarTodos_cuandoHayProductos_debeDevolverListaDeProductos() {
        // Arrange
        when(repositorioProducto.findAll()).thenReturn(List.of(producto));

        // Act
        List<Producto> productos = servicioProducto.listarTodos();

        // Assert
        assertFalse(productos.isEmpty());
        assertEquals(1, productos.size());
    }

    @Test
    void listarTodos_cuandoNoHayProductos_debeDevolverListaVacia() {
        // Arrange
        when(repositorioProducto.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Producto> productos = servicioProducto.listarTodos();

        // Assert
        assertTrue(productos.isEmpty());
    }
}
