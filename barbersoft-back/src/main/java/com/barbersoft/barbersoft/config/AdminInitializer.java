package com.barbersoft.barbersoft.config;

import com.barbersoft.barbersoft.entity.Usuario;
import com.barbersoft.barbersoft.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Value("${app.admin.name}")
    private String nombre;

    @Value("${app.admin.email}")
    private String email;

    @Value("${app.admin.password}")
    private String password;

    @Bean
    public ApplicationRunner crearAdministradorInicial(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {

        return args -> {

            if (email == null || email.isBlank()
                    || password == null || password.isBlank()) {

                throw new IllegalStateException(
                        "Debe configurar ADMIN_EMAIL y ADMIN_PASSWORD"
                );
            }

            // No crear otro administrador si ya existe.
            if (usuarioRepository.existsByEmail(email)) {
                return;
            }

            Usuario administrador = new Usuario();

            administrador.setNombre(nombre);
            administrador.setEmail(email);
            administrador.setPassword(
                    passwordEncoder.encode(password)
            );

            usuarioRepository.save(administrador);

            System.out.println(
                    "Administrador inicial creado correctamente."
            );
        };
    }
}