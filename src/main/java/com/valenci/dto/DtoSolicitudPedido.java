package com.valenci.dto;

import com.valenci.entidades.MetodoPago; // Asegúrate de importar tu Enum
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class DtoSolicitudPedido {

    // Explicación: Ya no necesitamos el idCliente. El backend lo sabrá por el token JWT.

    @NotEmpty(message = "El pedido debe contener al menos un producto.")
    private List<@Valid DtoSolicitudDetallePedido> detalles;

    @NotNull(message = "Se debe especificar un método de pago.")
    private MetodoPago metodoPago;
}
