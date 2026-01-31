package com.valenci.controladores;

import com.valenci.entidades.Producto;
import com.valenci.entidades.Proveedor;
import com.valenci.dto.DtoRespuestaProducto;
import com.valenci.dto.DtoSolicitudProducto;
import com.valenci.mapper.MapeadorProducto;
import com.valenci.servicios.ServicioProducto;
import com.valenci.servicios.ServicioUsuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ControladorProducto {

    private final ServicioProducto servicioProducto;
    private final ServicioUsuario servicioUsuario;

    public ControladorProducto(ServicioProducto servicioProducto, ServicioUsuario servicioUsuario) {
        this.servicioProducto = servicioProducto;
        this.servicioUsuario = servicioUsuario;
    }

    @GetMapping
    public ResponseEntity<List<DtoRespuestaProducto>> listarTodos() {
        List<Producto> productos = servicioProducto.listarTodos();
        return ResponseEntity.ok(MapeadorProducto.aListaDto(productos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DtoRespuestaProducto> buscarPorId(@PathVariable int id) {
        return servicioProducto.buscarPorId(id)
                .map(producto -> ResponseEntity.ok(MapeadorProducto.aDto(producto)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    @PostMapping
    // --- CORRECCIÓN CRÍTICA DE SEGURIDAD ---
    // El rol definido en la entidad es 'ADMINISTRADOR', no 'ADMIN'.
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaProducto> crear(@Valid @RequestBody DtoSolicitudProducto dto) {
        Producto nuevoProducto = MapeadorProducto.aEntidad(dto);

        Proveedor proveedor = (Proveedor) servicioUsuario.buscarPorId(dto.getIdProveedor())
                .filter(u -> u instanceof Proveedor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proveedor no encontrado con ID: " + dto.getIdProveedor()));

        nuevoProducto.setProveedor(proveedor);
        Producto productoGuardado = servicioProducto.crear(nuevoProducto);
        return new ResponseEntity<>(MapeadorProducto.aDto(productoGuardado), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<DtoRespuestaProducto> actualizar(@PathVariable int id, @Valid @RequestBody DtoSolicitudProducto dto) {
        Producto datosParaActualizar = MapeadorProducto.aEntidad(dto);

        Proveedor proveedor = (Proveedor) servicioUsuario.buscarPorId(dto.getIdProveedor())
                .filter(u -> u instanceof Proveedor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Proveedor no encontrado con ID: " + dto.getIdProveedor()));

        datosParaActualizar.setProveedor(proveedor);

        try {
            Producto productoActualizado = servicioProducto.actualizar(id, datosParaActualizar);
            return ResponseEntity.ok(MapeadorProducto.aDto(productoActualizado));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        try {
            servicioProducto.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}