package com.barbersoft.barbersoft.dto;

import java.time.LocalDate;
import java.util.List;

public class DisponibilidadResponse {

    private Long barberoId;
    private LocalDate fecha;
    private Integer duracionTotal;
    private List<String> horariosDisponibles;

    public DisponibilidadResponse() {
    }

    public DisponibilidadResponse(
            Long barberoId,
            LocalDate fecha,
            Integer duracionTotal,
            List<String> horariosDisponibles
    ) {
        this.barberoId = barberoId;
        this.fecha = fecha;
        this.duracionTotal = duracionTotal;
        this.horariosDisponibles = horariosDisponibles;
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

    public Integer getDuracionTotal() {
        return duracionTotal;
    }

    public void setDuracionTotal(Integer duracionTotal) {
        this.duracionTotal = duracionTotal;
    }

    public List<String> getHorariosDisponibles() {
        return horariosDisponibles;
    }

    public void setHorariosDisponibles(List<String> horariosDisponibles) {
        this.horariosDisponibles = horariosDisponibles;
    }
}