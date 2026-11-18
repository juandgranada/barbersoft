package com.barbersoft.barbersoft.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservaListadoResponse {

    private Long id;
    private String codigoReserva;
    private String nombreCliente;
    private String nombreBarbero;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private BigDecimal total;
    private String estado;

    public ReservaListadoResponse(
            Long id,
            String codigoReserva,
            String nombreCliente,
            String nombreBarbero,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            BigDecimal total,
            String estado
    ) {
        this.id = id;
        this.codigoReserva = codigoReserva;
        this.nombreCliente = nombreCliente;
        this.nombreBarbero = nombreBarbero;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.total = total;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public String getCodigoReserva() {
        return codigoReserva;
    }

    public String getNombreCliente() {
        return nombreCliente;
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

    public String getEstado() {
        return estado;
    }
}