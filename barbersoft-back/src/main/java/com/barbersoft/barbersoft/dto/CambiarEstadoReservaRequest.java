package com.barbersoft.barbersoft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CambiarEstadoReservaRequest {

    @NotBlank(message = "Debe indicar el nuevo estado de la reserva")
    @Pattern(
            regexp = "CONFIRMADA|CANCELADA|COMPLETADA",
            message = "El estado debe ser CONFIRMADA, CANCELADA o COMPLETADA"
    )
    private String estado;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}