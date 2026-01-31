package com.valenci.servicios;

import com.valenci.entidades.Factura;
import com.valenci.entidades.Pedido;
import com.valenci.repositorios.RepositorioFactura;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de facturas.
 * Contiene toda la lógica de negocio para crear y consultar facturas.
 */
@Service
@Slf4j
public class ServicioFacturaImp implements ServicioFactura {

    private final RepositorioFactura repositorioFactura;

    // Inyectamos el valor de la tasa de IVA desde application.properties
    @Value("${app.iva.tasa}")
    private BigDecimal tasaIva;

    public ServicioFacturaImp(RepositorioFactura repositorioFactura) {
        this.repositorioFactura = repositorioFactura;
    }

    @Override
    @Transactional // Este método modifica la base de datos
    public Factura crearFacturaParaPedido(Pedido pedido) {
        log.info("ServicioFactura: Creando factura para el pedido ID: {}", pedido.getIdPedido());
        // --- LÓGICA EXTRAÍDA DE SERVICIOPEDIDO ---
        // Toda la lógica de creación de la factura ahora vive aquí.
        try {
            BigDecimal iva = pedido.getTotalPedido().multiply(this.tasaIva);
            Factura nuevaFactura = new Factura();
            nuevaFactura.setPedido(pedido);
            nuevaFactura.setFechaFactura(LocalDateTime.now());
            nuevaFactura.setTotalFactura(pedido.getTotalPedido());
            nuevaFactura.setIva(iva);
            Factura facturaGuardada = repositorioFactura.save(nuevaFactura);
            log.info("Factura ID {} generada exitosamente para el pedido ID: {}", facturaGuardada.getIdFactura(), pedido.getIdPedido());
            return facturaGuardada;
        } catch (Exception e) {
            log.error("¡ERROR CRÍTICO! Ocurrió un error al generar la factura para el pedido ID {}.", pedido.getIdPedido(), e);
            // Relanzamos como una excepción de runtime para que la transacción principal (del pago) haga rollback.
            throw new RuntimeException("Error al generar la factura.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Factura> buscarPorId(int idFactura) {
        // Usamos el método optimizado del repositorio que ya tenías
        return repositorioFactura.findById(idFactura);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Factura> buscarPorIdPedido(int idPedido) {
        return repositorioFactura.findByPedidoIdPedido(idPedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> listarTodas() {
        // Usamos el método optimizado del repositorio que ya tenías
        return repositorioFactura.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> listarPorCliente(int idCliente) {
        return repositorioFactura.findByPedidoClienteId(idCliente);
    }
}
