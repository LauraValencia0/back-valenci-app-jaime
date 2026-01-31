package com.valenci.controladores;

import com.valenci.entidades.*;
import com.valenci.dto.DtoRespuestaPedido;
import com.valenci.dto.DtoSolicitudPedido;
import com.valenci.mapper.MapeadorPedido;
import com.valenci.servicios.ServicioPedido;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class ControladorPedido {

    private final ServicioPedido servicioPedido;

    public ControladorPedido(ServicioPedido servicioPedido) {
        this.servicioPedido = servicioPedido;
    }

    /**
     * Endpoint seguro para que un CLIENTE autenticado cree un nuevo pedido
     * a partir de su carrito de compras.
     */
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<DtoRespuestaPedido> crearPedidoDesdeCarrito(
            @AuthenticationPrincipal Usuario usuarioAutenticado,
            @Valid @RequestBody DtoSolicitudPedido dto) {

        try {
            // 1. Preparamos la entidad Pedido, asignando el cliente autenticado.
            Pedido nuevoPedido = new Pedido();
            nuevoPedido.setCliente((Cliente) usuarioAutenticado);

            // 2. Convertimos los DTOs de detalle en entidades DetallePedido.
            List<DetallePedido> detalles = dto.getDetalles().stream().map(detalleDto -> {
                DetallePedido detalle = new DetallePedido();
                // Creamos un objeto Producto "parcial" solo con el ID para la relación.
                Producto productoParcial = new Producto();
                productoParcial.setIdProducto(detalleDto.getIdProducto());
                detalle.setProducto(productoParcial);
                detalle.setCantidad(detalleDto.getCantidad());
                return detalle;
            }).collect(Collectors.toList());
            nuevoPedido.setDetalles(detalles);

            // 3. Llamamos al servicio 'crear' para guardar el pedido y descontar stock.
            Pedido pedidoCreado = servicioPedido.crear(nuevoPedido);

            // 4. Inmediatamente después, registramos el pago usando la firma segura del método.
            servicioPedido.registrarPago(
                    pedidoCreado.getIdPedido(),
                    dto.getMetodoPago() // El método de pago viene del DTO
            );

            // 5. Buscamos el pedido final (ahora PAGADO y con factura) para devolverlo.
            // Usamos orElseThrow para manejar el caso improbable de que no se encuentre.
            Pedido pedidoFinal = servicioPedido.buscarPorId(pedidoCreado.getIdPedido())
                    .orElseThrow(() -> new IllegalStateException("Error crítico: No se pudo recuperar el pedido final después del pago."));

            // 6. Mapeamos a DTO y devolvemos la respuesta.
            return new ResponseEntity<>(MapeadorPedido.aDtoRespuesta(pedidoFinal), HttpStatus.CREATED);

        } catch (IllegalArgumentException | IllegalStateException e) {
            // Capturamos errores de negocio (stock, producto no existe) y devolvemos 400 Bad Request.
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // --- Endpoints de Administración ---

    /**
     * Endpoint para que un ADMIN busque un pedido específico por ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')") // Rol corregido
    public ResponseEntity<DtoRespuestaPedido> buscarPorId(@PathVariable int id) {
        return servicioPedido.buscarPorId(id)
                .map(MapeadorPedido::aDtoRespuesta) // Convertimos a DTO
                .map(ResponseEntity::ok) // Si se encuentra, devolvemos 200 OK
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado con ID: " + id)); // Si no, 404 Not Found
    }

    /**
     * Endpoint para que un ADMIN actualice el estado de un pedido.
     */
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR')") // Rol corregido
    public ResponseEntity<DtoRespuestaPedido> actualizarEstado(@PathVariable int id, @RequestParam EstadoPedido nuevoEstado) {
        try {
            servicioPedido.actualizarEstado(id, nuevoEstado);
            // Buscamos y devolvemos el pedido actualizado para confirmar el cambio
            return servicioPedido.buscarPorId(id)
                    .map(MapeadorPedido::aDtoRespuesta)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado con ID: " + id)); // Aunque raro, podría pasar si se elimina justo después
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()); // Si el ID inicial no existe
        }
    }

    /**
     * Endpoint para que un ADMIN liste pedidos, con filtros opcionales.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')") // Rol corregido
    public ResponseEntity<List<DtoRespuestaPedido>> listar(
            // Parámetros opcionales para filtrar
            @RequestParam(required = false) EstadoPedido estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Integer idProducto) {

        List<Pedido> pedidos;
        // Aplicamos el filtro correspondiente según los parámetros recibidos
        if (estado != null) {
            pedidos = servicioPedido.listarPorEstado(estado);
        } else if (fecha != null) {
            pedidos = servicioPedido.listarPorFecha(fecha);
        } else if (idProducto != null) {
            pedidos = servicioPedido.listarPorProducto(idProducto);
        } else {
            // Si no hay filtros, listamos todos
            pedidos = servicioPedido.listarTodos();
        }

        // Mapeamos la lista de entidades a DTOs
        List<DtoRespuestaPedido> dtos = pedidos.stream()
                .map(MapeadorPedido::aDtoRespuesta)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}