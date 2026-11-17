package com.barbersoft.barbersoft.controller;

import com.barbersoft.barbersoft.dto.auth.LoginRequest;
import com.barbersoft.barbersoft.dto.auth.LoginResponse;
import com.barbersoft.barbersoft.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // HU08: iniciar sesión como administrador.
    @PostMapping("/login")
    public LoginResponse iniciarSesion(
            @Valid @RequestBody LoginRequest request
    ) {
        return authService.iniciarSesion(request);
    }
}