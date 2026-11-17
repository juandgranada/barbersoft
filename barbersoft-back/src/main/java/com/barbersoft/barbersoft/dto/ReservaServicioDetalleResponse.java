package com.barbersoft.barbersoft.dto;

import java.math.BigDecimal;

public class ReservaServicioDetalleResponse {

    private Long servicioId;
    private String nombreServicio;
    private BigDecimal precio;
    private Integer duracionMinutos;

    public ReservaServicioDetalleResponse(
            Long servicioId,
            String nombreServicio,
            BigDecimal precio,
            Integer duracionMinutos
    ) {
        this.servicioId = servicioId;
        this.nombreServicio = nombreServicio;
        this.precio = precio;
        this.duracionMinutos = duracionMinutos;
    }

    public Long getServicioId() {
        return servicioId;
    }

    public String getNombreServicio() {
        return nombreServicio;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }
}