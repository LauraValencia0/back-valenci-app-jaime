package com.valenci.servicios;

import com.valenci.entidades.*;
import com.valenci.repositorios.RepositorioPago;
import com.valenci.repositorios.RepositorioPedido;
import com.valenci.repositorios.RepositorioProducto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas para ServicioPedidoImpl Refactorizado")
class ServicioPedidoImplTest {

    @Mock
    private RepositorioPedido repositorioPedido;
    @Mock
    private RepositorioProducto repositorioProducto;
    @Mock
    private RepositorioPago repositorioPago;

    // --- ¡CAMBIO #1! ---
    // Explicación: Ya no necesitamos el RepositorioFactura aquí.
    // Ahora, el ServicioPedido depende del ServicioFactura.
    @Mock
    private ServicioFactura servicioFactura;

    @InjectMocks
    private ServicioPedidoImpl servicioPedido;

    private Cliente clienteDePrueba;
    private Producto productoDePrueba;
    private Pedido pedidoDePrueba;

    @BeforeEach
    void setUp() {
        clienteDePrueba = new Cliente("Cliente de Prueba", "cliente@test.com", "pass", "Calle Falsa 123");
        clienteDePrueba.setId(1);

        productoDePrueba = new Producto();
        productoDePrueba.setIdProducto(10);
        productoDePrueba.setNombreProducto("Producto Test");
        productoDePrueba.setPrecio(new BigDecimal("100.00"));
        productoDePrueba.setCantidad(20);

        DetallePedido detalle = new DetallePedido();
        detalle.setProducto(productoDePrueba);
        detalle.setCantidad(2);

        pedidoDePrueba = new Pedido();
        pedidoDePrueba.setIdPedido(1);
        pedidoDePrueba.setCliente(clienteDePrueba);
        pedidoDePrueba.setDetalles(Collections.singletonList(detalle));
        pedidoDePrueba.setEstadoPedido(EstadoPedido.PENDIENTE);
        pedidoDePrueba.setTotalPedido(new BigDecimal("200.00"));

        // --- ¡CAMBIO #2! ---
        // Explicación: Eliminamos la línea 'ReflectionTestUtils.setField'
        // porque el campo 'tasaIva' ya no existe en ServicioPedidoImpl.
    }

    @Test
    @DisplayName("Crear pedido cuando hay stock suficiente")
    void crear_cuandoHayStock_debeCrearPedidoYActualizarStock() {
        // (Sin cambios, esta prueba sigue siendo válida)
        when(repositorioProducto.findById(10)).thenReturn(Optional.of(productoDePrueba));
        when(repositorioPedido.save(any(Pedido.class))).thenReturn(pedidoDePrueba);

        Pedido resultado = servicioPedido.crear(pedidoDePrueba);

        assertNotNull(resultado);
        assertEquals(EstadoPedido.PENDIENTE, resultado.getEstadoPedido());
        assertEquals(0, new BigDecimal("200.00").compareTo(resultado.getTotalPedido()));

        ArgumentCaptor<Producto> productoCaptor = ArgumentCaptor.forClass(Producto.class);
        verify(repositorioProducto).save(productoCaptor.capture());
        assertEquals(18, productoCaptor.getValue().getCantidad());
    }

    @Test
    @DisplayName("Crear pedido cuando no hay stock suficiente debe lanzar excepción")
    void crear_cuandoNoHayStock_debeLanzarExcepcion() {
        // (Sin cambios, esta prueba sigue siendo válida)
        productoDePrueba.setCantidad(1);
        when(repositorioProducto.findById(10)).thenReturn(Optional.of(productoDePrueba));

        assertThrows(IllegalStateException.class, () -> servicioPedido.crear(pedidoDePrueba));
        verify(repositorioPedido, never()).save(any(Pedido.class));
    }

    @Test
    @DisplayName("Registrar pago para un pedido válido")
    void registrarPago_cuandoPedidoEsValido_debeDelegarCreacionDeFactura() {
        // Arrange
        when(repositorioPedido.findById(1)).thenReturn(Optional.of(pedidoDePrueba));

        // --- ¡CAMBIO #3! ---
        // Explicación: Simulamos ("mockeamos") la llamada al nuevo servicio.
        // Le decimos a Mockito: "Cuando alguien llame a 'servicioFactura.crearFacturaParaPedido',
        // simplemente devuelve una nueva Factura vacía para que la prueba no falle."
        when(servicioFactura.crearFacturaParaPedido(any(Pedido.class))).thenReturn(new Factura());

        // Act
        servicioPedido.registrarPago(1, MetodoPago.TARJETA_CREDITO);

        // Assert
        ArgumentCaptor<Pedido> pedidoCaptor = ArgumentCaptor.forClass(Pedido.class);
        ArgumentCaptor<Pago> pagoCaptor = ArgumentCaptor.forClass(Pago.class);

        verify(repositorioPedido).save(pedidoCaptor.capture());
        verify(repositorioPago).save(pagoCaptor.capture());

        // --- ¡CAMBIO #4! ---
        // Explicación: Ya no verificamos que el repositorio de facturas se guarde.
        // En su lugar, verificamos que se haya llamado al *servicio* de facturas,
        // confirmando que la responsabilidad ha sido delegada correctamente.
        verify(servicioFactura).crearFacturaParaPedido(pedidoDePrueba);

        assertEquals(EstadoPedido.PAGADO, pedidoCaptor.getValue().getEstadoPedido());
        assertEquals(MetodoPago.TARJETA_CREDITO, pagoCaptor.getValue().getMetodoPago());
        assertEquals(0, new BigDecimal("200.00").compareTo(pagoCaptor.getValue().getMonto()));
    }
}
