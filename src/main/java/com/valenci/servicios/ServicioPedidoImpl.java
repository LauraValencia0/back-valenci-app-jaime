package com.valenci.servicios;

import com.valenci.entidades.*;
import com.valenci.repositorios.RepositorioPago;
import com.valenci.repositorios.RepositorioPedido;
import com.valenci.repositorios.RepositorioProducto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ServicioPedidoImpl implements ServicioPedido {

    private final RepositorioPedido repositorioPedido;
    private final RepositorioProducto repositorioProducto;
    private final RepositorioPago repositorioPago;
    private final ServicioFactura servicioFactura;

    public ServicioPedidoImpl(RepositorioPedido repositorioPedido, RepositorioProducto repositorioProducto, RepositorioPago repositorioPago, ServicioFactura servicioFactura) {
        this.repositorioPedido = repositorioPedido;
        this.repositorioProducto = repositorioProducto;
        this.repositorioPago = repositorioPago;
        this.servicioFactura = servicioFactura;
    }

    @Override
    @Transactional
    public Pedido crear(Pedido pedido) {
        log.info("Iniciando creación de pedido para el cliente ID: {}", pedido.getCliente().getId());
        if (pedido == null || pedido.getCliente() == null || pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("El pedido, cliente o detalles no pueden ser nulos o vacíos.");
        }
        BigDecimal totalGeneral = BigDecimal.ZERO;
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto productoEnDB = repositorioProducto.findById(detalle.getProducto().getIdProducto())
                    .orElseThrow(() -> new IllegalArgumentException("El producto con ID " + detalle.getProducto().getIdProducto() + " no existe."));
            if (productoEnDB.getCantidad() < detalle.getCantidad()) {
                throw new IllegalStateException("Stock insuficiente para el producto: " + productoEnDB.getNombreProducto());
            }
            detalle.setPrecioUnitario(productoEnDB.getPrecio());
            BigDecimal subtotal = productoEnDB.getPrecio().multiply(BigDecimal.valueOf(detalle.getCantidad()));
            detalle.setSubtotal(subtotal);
            totalGeneral = totalGeneral.add(subtotal);
            productoEnDB.setCantidad(productoEnDB.getCantidad() - detalle.getCantidad());
            repositorioProducto.save(productoEnDB);
            detalle.setPedido(pedido);
        }
        pedido.setTotalPedido(totalGeneral);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstadoPedido(EstadoPedido.PENDIENTE);
        return repositorioPedido.save(pedido);
    }

    @Override
    @Transactional
    public void registrarPago(int idPedido, MetodoPago metodoPago) {
        log.info("Iniciando registro de pago para el pedido ID: {} con método: {}", idPedido, metodoPago);
        Pedido pedido = repositorioPedido.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("El pedido con ID " + idPedido + " no existe."));

        if (pedido.getEstadoPedido() != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException("El pedido no se encuentra en estado PENDIENTE.");
        }

        Pago nuevoPago = new Pago();
        nuevoPago.setPedido(pedido);
        nuevoPago.setMonto(pedido.getTotalPedido());
        nuevoPago.setFechaPago(LocalDateTime.now());
        nuevoPago.setMetodoPago(metodoPago);
        repositorioPago.save(nuevoPago);
        log.info("Pago registrado para el pedido ID: {}", idPedido);

        pedido.setEstadoPedido(EstadoPedido.PAGADO);
        repositorioPedido.save(pedido);
        log.info("Estado del pedido ID {} actualizado a PAGADO.", idPedido);

        // Delegamos la creación de la factura a su propio servicio
        servicioFactura.crearFacturaParaPedido(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(int idPedido) {
        return repositorioPedido.findById(idPedido);
    }

    // --- ¡MÉTODO ACTUALIZADO! ---
    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(int idCliente) {
        // Explicación: Ahora llamamos al nuevo método optimizado del repositorio.
        // Esto pre-carga todos los detalles y productos en una sola consulta,
        // eliminando la posibilidad de una LazyInitializationException.
        return repositorioPedido.findAllByClienteIdWithDetalles(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return repositorioPedido.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorEstado(EstadoPedido estado) {
        return repositorioPedido.findByEstadoPedido(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorFecha(LocalDate fecha) {
        return repositorioPedido.findByFecha(fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarPorProducto(int idProducto) {
        return repositorioPedido.findByProductoId(idProducto);
    }

    @Override
    @Transactional
    public void cancelar(int idPedido) {
        Pedido pedido = repositorioPedido.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("El pedido con ID " + idPedido + " no existe."));
        if (pedido.getEstadoPedido() == EstadoPedido.ENVIADO || pedido.getEstadoPedido() == EstadoPedido.ENTREGADO) {
            throw new IllegalStateException("No se puede cancelar un pedido que ya ha sido enviado o entregado.");
        }
        if (pedido.getEstadoPedido() != EstadoPedido.CANCELADO) {
            for (DetallePedido detalle : pedido.getDetalles()) {
                detalle.getProducto().setCantidad(detalle.getProducto().getCantidad() + detalle.getCantidad());
                repositorioProducto.save(detalle.getProducto());
            }
            pedido.setEstadoPedido(EstadoPedido.CANCELADO);
            repositorioPedido.save(pedido);
        }
    }

    @Override
    @Transactional
    public void actualizarEstado(int idPedido, EstadoPedido nuevoEstado) {
        Pedido pedido = repositorioPedido.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("El pedido con ID " + idPedido + " no existe."));
        pedido.setEstadoPedido(nuevoEstado);
        repositorioPedido.save(pedido);
    }
}

