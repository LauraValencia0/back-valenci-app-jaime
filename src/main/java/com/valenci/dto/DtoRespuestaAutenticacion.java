package com.valenci.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Esta clase representará la respuesta del endpoint de login.
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DtoRespuestaAutenticacion {
    private String token;
}
