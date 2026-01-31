package com.valenci;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        // Elige la contraseña temporal que quieras usar para todas las cuentas.
        String contraseñaPlana = "passwordseguro";

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String contraseñaHasheada = passwordEncoder.encode(contraseñaPlana);

        System.out.println("--- Tu Hash Válido (listo para copiar) ---");
        System.out.println(contraseñaHasheada);
        System.out.println("-------------------------------------------");
        System.out.println("La contraseña original es: " + contraseñaPlana);
    }
}
