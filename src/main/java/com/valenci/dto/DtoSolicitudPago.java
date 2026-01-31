package com.valenci.dto;

import com.valenci.entidades.MetodoPago;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DtoSolicitudPago {
    @NotNull(message = "El método de pago no puede ser nulo")
    private MetodoPago metodoPago;
}