package com.valenci.entidades;

import com.fasterxml.jackson.annotation.JsonCreator; // <-- ¡Importante!

public enum MetodoPago {
    TARJETA_CREDITO,
    TARJETA_DEBITO,
    TRANSFERENCIA,
    EFECTIVO;

    /**
     * Explicación Pedagógica (Metacognición):
     * La anotación @JsonCreator le dice a Jackson (la librería que convierte JSON a Java)
     * que, en lugar de intentar hacer una coincidencia exacta, debe usar ESTE método
     * para encontrar el valor correcto del Enum.
     * * @param value el texto que viene en el JSON (ej. "tarjeta_debito").
     * @return el valor correspondiente del Enum (ej. MetodoPago.TARJETA_DEBITO).
     */
    @JsonCreator
    public static MetodoPago fromString(String value) {
        if (value == null) {
            return null;
        }
        // Explicación: Usamos .toUpperCase() para convertir el texto de entrada a mayúsculas
        // y .valueOf() para encontrar la coincidencia en nuestro Enum.
        // Esto hace que la API acepte "tarjeta_debito", "Tarjeta_Debito", "TARJETA_DEBITO", etc.
        return MetodoPago.valueOf(value.toUpperCase());
    }
}
