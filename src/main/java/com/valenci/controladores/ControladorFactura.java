package com.valenci.controladores;

import com.valenci.dto.DtoRespuestaFactura;
import com.valenci.mapper.MapeadorFactura;
import com.valenci.servicios.ServicioFactura; // <-- ¡CAMBIO CLAVE! Inyectamos la dependencia correcta.
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/facturas")
// --- CORRECCIÓN DE SEGURIDAD ---
// Corregimos el typo en el nombre del rol para que coincida con tu SecurityConfig.
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ControladorFactura {

    // Ahora dependemos de la interfaz correcta: ServicioFactura.
    private final ServicioFactura servicioFactura;

    // Actualizamos el constructor para inyectar la nueva dependencia.
    public ControladorFactura(ServicioFactura servicioFactura) {
        this.servicioFactura = servicioFactura;
    }

    /**
     * Endpoint para que el ADMIN liste todas las facturas.
     * Llama al nuevo servicio de facturas.
     */
    @GetMapping
    public ResponseEntity<List<DtoRespuestaFactura>> listarTodas() {
        // Explicación: El controlador ahora solo habla con el servicio de facturas.
        // 1. servicioFactura.listarTodas() devuelve List<Factura> (Entidades)
        // 2. Mapeamos esa lista de entidades a una lista de DTOs para la respuesta.
        List<DtoRespuestaFactura> dtos = servicioFactura.listarTodas().stream()
                .map(MapeadorFactura::aDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    /**
     * Endpoint para que el ADMIN busque una factura por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DtoRespuestaFactura> buscarPorId(@PathVariable int id) {
        // El servicio nos devuelve un Optional<Factura>
        return servicioFactura.buscarPorId(id)
                .map(MapeadorFactura::aDto) // Mapeamos la entidad a DTO
                .map(ResponseEntity::ok)   // Si existe, envolvemos en 200 OK
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Factura no encontrada con ID: " + id)); // Si no, 404
    }

    /**
     * Endpoint para que el ADMIN liste facturas por el ID de un cliente.
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<DtoRespuestaFactura>> listarPorCliente(@PathVariable int idCliente) {
        // Lógica idéntica a listarTodas, pero llamando al método filtrado.
        List<DtoRespuestaFactura> dtos = servicioFactura.listarPorCliente(idCliente).stream()
                .map(MapeadorFactura::aDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}