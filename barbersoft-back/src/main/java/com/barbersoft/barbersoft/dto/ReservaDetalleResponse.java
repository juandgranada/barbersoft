package com.barbersoft.barbersoft.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReservaDetalleResponse {

    private Long id;
    private String codigoReserva;
    private String estado;

    private String nombreCliente;
    private String telefonoCliente;
    private String emailCliente;

    private Long barberoId;
    private String nombreBarbero;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private BigDecimal total;
    private String observaciones;

    private List<ReservaServicioDetalleResponse> servicios;

    public ReservaDetalleResponse(
            Long id,
            String codigoReserva,
            String estado,
            String nombreCliente,
            String telefonoCliente,
            String emailCliente,
            Long barberoId,
            String nombreBarbero,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal total,
            String observaciones,
            List<ReservaServicioDetalleResponse> servicios
    ) {
        this.id = id;
        this.codigoReserva = codigoReserva;
        this.estado = estado;
        this.nombreCliente = nombreCliente;
        this.telefonoCliente = telefonoCliente;
        this.emailCliente = emailCliente;
        this.barberoId = barberoId;
        this.nombreBarbero = nombreBarbero;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.total = total;
        this.observaciones = observaciones;
        this.servicios = servicios;
    }

    public Long getId() {
        return id;
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public String getEstado() {
        return estado;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getTelefonoCliente() {
        return telefonoCliente;
    }

    public String getEmailCliente() {
        return emailCliente;
    }

    public Long getBarberoId() {
        return barberoId;
    }

    public String getNombreBarbero() {
        return nombreBarbero;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public List<ReservaServicioDetalleResponse> getServicios() {
        return servicios;
    }
}