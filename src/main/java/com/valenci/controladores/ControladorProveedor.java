package com.valenci.controladores;

import com.valenci.entidades.Proveedor;
import com.valenci.dto.DtoSolicitudProveedor;
import com.valenci.dto.DtoRespuestaProveedor;
import com.valenci.mapper.MapeadorProveedor;
import com.valenci.servicios.ServicioProveedor;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
// --- CORRECCIÓN CRÍTICA DE SEGURIDAD ---
// El rol de administrador es 'ADMINISTRADOR'.
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class ControladorProveedor {

    private final ServicioProveedor servicioProveedor;

    public ControladorProveedor(ServicioProveedor servicioProveedor) {
        this.servicioProveedor = servicioProveedor;
    }

    @PostMapping
    public ResponseEntity<DtoRespuestaProveedor> crear(@Valid @RequestBody DtoSolicitudProveedor dtoSolicitud) {
        try {
            Proveedor nuevoProveedor = MapeadorProveedor.aEntidad(dtoSolicitud);
            Proveedor proveedorGuardado = servicioProveedor.crear(nuevoProveedor);
            return new ResponseEntity<>(MapeadorProveedor.aDtoRespuesta(proveedorGuardado), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<DtoRespuestaProveedor>> listarTodos() {
        List<Proveedor> proveedores = servicioProveedor.listarTodos();
        // Usamos el mapeador que tú creaste, que es más completo
        return ResponseEntity.ok(MapeadorProveedor.aDtoRespuestaLista(proveedores));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DtoRespuestaProveedor> buscarPorId(@PathVariable int id) {
        return servicioProveedor.buscarPorId(id)
                .map(MapeadorProveedor::aDtoRespuesta)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado con ID: " + id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DtoRespuestaProveedor> actualizar(@PathVariable int id, @Valid @RequestBody DtoSolicitudProveedor dtoSolicitud) {
        try {
            Proveedor datosParaActualizar = MapeadorProveedor.aEntidad(dtoSolicitud);
            Proveedor proveedorActualizado = servicioProveedor.actualizar(id, datosParaActualizar);
            return ResponseEntity.ok(MapeadorProveedor.aDtoRespuesta(proveedorActualizado));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        try {
            servicioProveedor.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar el proveedor, tiene productos asociados.");
        }
    }
}