package com.barbersoft.barbersoft.service;

import com.barbersoft.barbersoft.dto.auth.LoginRequest;
import com.barbersoft.barbersoft.dto.auth.LoginResponse;
import com.barbersoft.barbersoft.entity.Usuario;
import com.barbersoft.barbersoft.repository.UsuarioRepository;
import com.barbersoft.barbersoft.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse iniciarSesion(LoginRequest request) {

        try {
            // Validar correo y contraseña mediante Spring Security.
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail().trim(),
                                    request.getPassword()
                            )
                    );

            String email = authentication.getName();

            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Credenciales incorrectas"
                    ));

            // Generar el token del administrador.
            String token = jwtService.generarToken(email);

            return new LoginResponse(
                    token,
                    "Bearer",
                    usuario.getNombre(),
                    usuario.getEmail(),
                    jwtService.obtenerTiempoExpiracion()
            );

        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Correo electrónico o contraseña incorrectos"
            );
        }
    }
}