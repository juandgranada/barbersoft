package com.barbersoft.barbersoft.dto;

public class AsignacionServicioRequest {

    private Long barberoId;
    private Long servicioId;

    public AsignacionServicioRequest() {
    }

    public Long getBarberoId() {
        return barberoId;
    }

    public void setBarberoId(Long barberoId) {
        this.barberoId = barberoId;
    }

    public Long getServicioId() {
        return servicioId;
    }

    public void setServicioId(Long servicioId) {
        this.servicioId = servicioId;
    }
}