package com.valenci.controladores;

import com.valenci.dto.DtoSolicitudAutenticacion;
import com.valenci.dto.DtoRespuestaAutenticacion;
import com.valenci.servicios.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class ControladorAutenticacion {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public ControladorAutenticacion(JwtService jwtService, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Endpoint para iniciar sesión (autenticar).
     * Recibe credenciales, las valida y devuelve un token JWT si son correctas.
     */
    @PostMapping("/login")
    public ResponseEntity<DtoRespuestaAutenticacion> iniciarSesion(@RequestBody DtoSolicitudAutenticacion authRequest) {
        // Spring Security se encarga de la autenticación. Si las credenciales son incorrectas,
        // lanzará una excepción que será manejada automáticamente.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getCorreo(),
                        authRequest.getContrasena()
                )
        );

        // Si la autenticación fue exitosa, buscamos al usuario para generar el token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getCorreo());

        // Generamos el token
        final String token = jwtService.generateToken(userDetails);

        // Devolvemos el token en la respuesta
        return ResponseEntity.ok(DtoRespuestaAutenticacion.builder().token(token).build());
    }
}
