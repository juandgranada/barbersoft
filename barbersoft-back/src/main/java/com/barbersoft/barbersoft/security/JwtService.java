package com.barbersoft.barbersoft.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.expiration-ms}")
    private long expiration;

    // Generar la clave criptográfica a partir del secreto Base64.
    private SecretKey obtenerClave() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generar un token para el administrador.
    public String generarToken(String email) {

        Date ahora = new Date();
        Date fechaExpiracion = new Date(
                ahora.getTime() + expiration
        );

        return Jwts.builder()
                .subject(email)
                .issuedAt(ahora)
                .expiration(fechaExpiracion)
                .signWith(obtenerClave())
                .compact();
    }

    // Obtener el correo electrónico guardado en el token.
    public String extraerEmail(String token) {
        return obtenerClaims(token).getSubject();
    }

    // Comprobar que el token no haya expirado.
    public boolean esTokenValido(String token) {
        try {
            obtenerClaims(token);
            return true;
        } catch (io.jsonwebtoken.JwtException |
                 IllegalArgumentException e) {
            return false;
        }
    }

    private Claims obtenerClaims(String token) {
        return Jwts.parser()
                .verifyWith(obtenerClave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long obtenerTiempoExpiracion() {
        return expiration;
    }
}