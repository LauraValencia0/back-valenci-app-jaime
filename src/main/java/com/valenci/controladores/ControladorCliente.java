package com.valenci.controladores;

import com.valenci.dto.DtoRespuestaUsuario;
import com.valenci.dto.DtoSolicitudCliente;
import com.valenci.entidades.Cliente;
import com.valenci.mapper.MapeadorUsuario;
import com.valenci.servicios.ServicioCliente;
import com.valenci.servicios.ServicioUsuario; // Import necesario
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ControladorCliente {

    private final ServicioCliente servicioCliente;
    private final ServicioUsuario servicioUsuario;

    public ControladorCliente(ServicioCliente servicioCliente, ServicioUsuario servicioUsuario) {
        this.servicioCliente = servicioCliente;
        this.servicioUsuario = servicioUsuario;
    }

    @PostMapping("/registro")
    public ResponseEntity<DtoRespuestaUsuario> registrarCliente(@Valid @RequestBody DtoSolicitudCliente dtoCliente) {
        try {
            Cliente nuevoCliente = MapeadorUsuario.aEntidadCliente(dtoCliente);
            servicioUsuario.registrar(nuevoCliente);
            return new ResponseEntity<>(MapeadorUsuario.aDtoRespuesta(nuevoCliente), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<DtoRespuestaUsuario>> listarTodosLosClientes() {
        List<Cliente> clientes = servicioCliente.listarTodos();
        // Esta línea ahora compila gracias a la corrección en el mapeador.
        return ResponseEntity.ok(MapeadorUsuario.aListaDtoRespuesta(clientes));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaUsuario> obtenerClientePorId(@PathVariable int id) {
        return servicioCliente.buscarPorId(id)
                .map(MapeadorUsuario::aDtoRespuesta)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con ID: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaUsuario> actualizarCliente(@PathVariable int id, @Valid @RequestBody DtoSolicitudCliente dtoCliente) {
        try {
            Cliente datosParaActualizar = MapeadorUsuario.aEntidadCliente(dtoCliente);
            Cliente clienteActualizado = servicioCliente.actualizar(id, datosParaActualizar);
            return ResponseEntity.ok(MapeadorUsuario.aDtoRespuesta(clienteActualizado));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarCliente(@PathVariable int id) {
        try {
            servicioCliente.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar el cliente. Puede tener pedidos asociados.");
        }
    }
}
