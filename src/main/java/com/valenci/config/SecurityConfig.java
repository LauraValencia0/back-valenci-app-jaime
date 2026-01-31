package com.valenci.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Asegúrate de tener este import
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar @PreAuthorize en los controladores
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    // URLs públicas (sin necesidad de token)
    private static final String[] PUBLIC_URLS = {
            "/api/auth/**",            // Endpoints de login/registro
            "/api/clientes/registro",  // Endpoint específico para registrar clientes
            "/v3/api-docs/**",         // Documentación Swagger/OpenAPI
            "/swagger-ui/**",          // UI de Swagger
            "/webjars/**"              // Recursos web para Swagger
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF (común en APIs stateless)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Aplica configuración CORS
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll() // Permite acceso público a estas URLs
                        .requestMatchers(HttpMethod.GET, "/api/productos", "/api/productos/**").permitAll() // Permite VER productos sin login

                        // --- ¡REGLAS CORREGIDAS Y COMPLETAS! ---
                        // Explicación Pedagógica:
                        // Permitimos explícitamente las acciones comunes para usuarios autenticados.
                        // La autorización específica por rol (@PreAuthorize) se encargará del resto.
                        .requestMatchers(HttpMethod.POST, "/api/pedidos").authenticated() // Crear pedido (Cliente)
                        .requestMatchers(HttpMethod.GET, "/api/cuenta/**").authenticated() // Ver perfil e historial (Cliente/Admin)
                        .requestMatchers(HttpMethod.PUT, "/api/cuenta/**").authenticated() // Actualizar perfil/contraseña (Cliente/Admin)

                        // --- REGLAS DE ADMINISTRACIÓN ---
                        // Explicación: Para simplificar, permitimos que cualquier usuario autenticado INTENTE acceder
                        // a las rutas de admin. La anotación @PreAuthorize('hasRole("ADMINISTRADOR")')
                        // en los controladores hará el bloqueo real si el rol no es correcto.
                        .requestMatchers("/api/admin/**").authenticated()
                        .requestMatchers("/api/clientes/**").authenticated() // Para GET, PUT, DELETE de admin
                        .requestMatchers("/api/proveedores/**").authenticated() // Para CRUD de admin
                        .requestMatchers("/api/facturas/**").authenticated() // Para GET de admin

                        // CUALQUIER OTRA petición requiere autenticación (buena práctica de seguridad por defecto)
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // API sin estado (usa JWT)
                .formLogin(AbstractHttpConfigurer::disable) // Deshabilita login por formulario
                .httpBasic(AbstractHttpConfigurer::disable) // Deshabilita autenticación HTTP Basic
                .authenticationProvider(authenticationProvider) // Configura el proveedor de autenticación personalizado
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Añade el filtro JWT

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Configuración estándar de CORS para permitir peticiones desde tu frontend
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
