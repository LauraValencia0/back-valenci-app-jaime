package com.valenci.controladores;

import com.valenci.dto.DtoCambioContrasena;
import com.valenci.dto.DtoPedidoHistorial;
import com.valenci.dto.DtoRespuestaUsuario;
import com.valenci.dto.DtoSolicitudActualizacionPerfil;
import com.valenci.entidades.Cliente;
import com.valenci.entidades.Usuario;
import com.valenci.mapper.MapeadorPedido;
import com.valenci.mapper.MapeadorUsuario;
import com.valenci.repositorios.RepositorioUsuario;
import com.valenci.servicios.ServicioFactura; // <-- ¡NUEVA DEPENDENCIA!
import com.valenci.servicios.ServicioPedido;
import com.valenci.servicios.ServicioUsuario;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cuenta")
@Slf4j
public class ControladorCuenta {

    private final ServicioPedido servicioPedido;
    private final ServicioUsuario servicioUsuario;
    private final RepositorioUsuario repositorioUsuario;
    private final ServicioFactura servicioFactura; // <-- ¡CAMBIO CLAVE! Se añade el nuevo servicio.

    // --- CONSTRUCTOR ACTUALIZADO ---
    // El constructor ahora inyecta todas las dependencias necesarias.
    public ControladorCuenta(ServicioPedido servicioPedido,
                             ServicioUsuario servicioUsuario,
                             RepositorioUsuario repositorioUsuario,
                             ServicioFactura servicioFactura) { // <-- Se añade al constructor.
        this.servicioPedido = servicioPedido;
        this.servicioUsuario = servicioUsuario;
        this.repositorioUsuario = repositorioUsuario;
        this.servicioFactura = servicioFactura;
    }

    // --- MÉTODOS DE PERFIL Y CONTRASEÑA (Sin cambios) ---

    @GetMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DtoRespuestaUsuario> verMiPerfil(@AuthenticationPrincipal Usuario usuarioAutenticado) {
        log.info("Solicitud de perfil para el usuario ID: {}", usuarioAutenticado.getId());
        return ResponseEntity.ok(MapeadorUsuario.aDtoRespuesta(usuarioAutenticado));
    }

    @PutMapping("/perfil")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DtoRespuestaUsuario> actualizarMiPerfil(
            @AuthenticationPrincipal Usuario usuarioAutenticado,
            @Valid @RequestBody DtoSolicitudActualizacionPerfil dto) {

        log.info("Solicitud de actualización de perfil para el usuario ID: {}", usuarioAutenticado.getId());
        usuarioAutenticado.setNombre(dto.getNombre());

        if (usuarioAutenticado instanceof Cliente) {
            ((Cliente) usuarioAutenticado).setDireccionEnvio(dto.getDireccionEnvio());
        }

        Usuario usuarioActualizado = repositorioUsuario.save(usuarioAutenticado);
        return ResponseEntity.ok(MapeadorUsuario.aDtoRespuesta(usuarioActualizado));
    }

    @PutMapping("/cambiar-contrasena")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cambiarMiContrasena(
            @AuthenticationPrincipal Usuario usuarioAutenticado,
            @Valid @RequestBody DtoCambioContrasena dto) {
        try {
            servicioUsuario.cambiarContrasena(usuarioAutenticado, dto.getContrasenaActual(), dto.getNuevaContrasena());
            return ResponseEntity.ok().build();
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        }
    }

    // --- MÉTODO DE HISTORIAL (Refactorizado) ---

    @GetMapping("/historial")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<DtoPedidoHistorial>> verMiHistorial(@AuthenticationPrincipal Usuario usuarioAutenticado) {
        log.info("Solicitud de historial para el cliente ID: {}", usuarioAutenticado.getId());

        // 1. Obtenemos los pedidos del cliente (como antes)
        List<DtoPedidoHistorial> historial = servicioPedido.listarPorCliente(usuarioAutenticado.getId()).stream()
                .map(pedido -> {
                    // 2. ¡CAMBIO CLAVE! Llamamos al servicio correcto para buscar la factura.
                    var factura = servicioFactura.buscarPorIdPedido(pedido.getIdPedido()).orElse(null);

                    // 3. Mapeamos usando los datos de ambos servicios
                    return MapeadorPedido.aDtoHistorial(pedido, factura);
                })
                .collect(Collectors.toList());

        log.info("Se encontraron {} registros en el historial para el cliente ID: {}", historial.size(), usuarioAutenticado.getId());
        return ResponseEntity.ok(historial);
    }
}