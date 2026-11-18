package com.barbersoft.barbersoft.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReservaResponse {

    private String codigoReserva;
    private String estado;
    private String nombreCliente;
    private String emailCliente;
    private Long barberoId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private BigDecimal total;
    private List<Long> serviciosIds;

    public ReservaResponse(
            String codigoReserva,
            String estado,
            String nombreCliente,
            String emailCliente,
            Long barberoId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal total,
            List<Long> serviciosIds
    ) {
        this.codigoReserva = codigoReserva;
        this.estado = estado;
        this.nombreCliente = nombreCliente;
        this.emailCliente = emailCliente;
        this.barberoId = barberoId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.total = total;
        this.serviciosIds = serviciosIds;
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

    public String getEmailCliente() {
        return emailCliente;
    }

    public Long getBarberoId() {
        return barberoId;
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

    public List<Long> getServiciosIds() {
        return serviciosIds;
    }
}