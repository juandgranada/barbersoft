package com.barbersoft.barbersoft.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    // Encriptador de contraseñas.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Administrador de autenticación de Spring Security.
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Configuración principal de seguridad.
    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // No utilizaremos sesiones tradicionales.
            // La autenticación se realizará mediante JWT.
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                    // Login público.
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/auth/login"
                    ).permitAll()

                    // Crear una reserva es público.
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/reservas"
                    ).permitAll()

                    // Consultar disponibilidad es público.
                    .requestMatchers(
                            HttpMethod.POST,
                            "/api/disponibilidad"
                    ).permitAll()

                    // Consultar servicios activos es público.
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/servicios/activos"
                    ).permitAll()

                    // Consultar barberos activos es público.
                    .requestMatchers(
                            HttpMethod.GET,
                            "/api/barberos/activos"
                    ).permitAll()

                    // Cualquier otra ruta necesita autenticación.
                    .anyRequest().hasRole("ADMIN")
            )

            // Respuesta cuando no hay autenticación válida.
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(
                            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                    )
                    .accessDeniedHandler((request, response, exception) ->
                            response.sendError(
                                    HttpStatus.FORBIDDEN.value(),
                                    "No tiene permisos para acceder a este recurso"
                            )
                    )
            )

            // Agregar el filtro JWT antes del filtro de usuario/contraseña.
            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    // Configuración CORS para permitir las peticiones desde React.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
                List.of("Authorization", "Content-Type")
        );

        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}