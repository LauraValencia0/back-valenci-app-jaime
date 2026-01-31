package com.valenci.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoResumenFactura {
    private int idFactura;
    private LocalDateTime fechaFactura;
    private BigDecimal totalFactura;
    // --- ¡CAMBIO CLAVE! ---
    // Añadimos el campo para el IVA que necesitamos en el frontend.
    private BigDecimal iva;
}
