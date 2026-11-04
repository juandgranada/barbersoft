package com.barbersoft.barbersoft.dto;

import java.time.LocalDate;
import java.util.List;

public class DisponibilidadRequest {

    private Long barberoId;
    private LocalDate fecha;
    private List<Long> serviciosIds;

    public DisponibilidadRequest() {
    }

    public Long getBarberoId() {
        return barberoId;
    }

    public void setBarberoId(Long barberoId) {
        this.barberoId = barberoId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public List<Long> getServiciosIds() {
        return serviciosIds;
    }

    public void setServiciosIds(List<Long> serviciosIds) {
        this.serviciosIds = serviciosIds;
    }
}