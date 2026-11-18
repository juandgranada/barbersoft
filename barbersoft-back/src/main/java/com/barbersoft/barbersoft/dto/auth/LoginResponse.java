package com.barbersoft.barbersoft.dto.auth;

public class LoginResponse {

    private String token;
    private String tipo;
    private String nombre;
    private String email;
    private long expiracion;

    public LoginResponse(
            String token,
            String tipo,
            String nombre,
            String email,
            long expiracion
    ) {
        this.token = token;
        this.tipo = tipo;
        this.nombre = nombre;
        this.email = email;
        this.expiracion = expiracion;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public long getExpiracion() {
        return expiracion;
    }
}