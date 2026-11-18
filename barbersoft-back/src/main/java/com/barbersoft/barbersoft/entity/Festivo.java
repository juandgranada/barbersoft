package com.barbersoft.barbersoft.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
    name = "festivos",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_festivos_fecha",
        columnNames = "fecha"
    )
)
public class Festivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Column(nullable = false)
    private Boolean activo = true;

    public Festivo() {}

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}