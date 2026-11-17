package com.barbersoft.barbersoft.controller;

import com.barbersoft.barbersoft.dto.CrearReservaRequest;
import com.barbersoft.barbersoft.dto.ReservaResponse;
import com.barbersoft.barbersoft.dto.ReservaListadoResponse;
import com.barbersoft.barbersoft.dto.ReservaDetalleResponse;
import com.barbersoft.barbersoft.dto.CambiarEstadoReservaRequest;

import com.barbersoft.barbersoft.entity.Reserva;
import com.barbersoft.barbersoft.service.ReservaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    // Crear una reserva
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse crearReserva(
            @Valid @RequestBody CrearReservaRequest request) {
        return reservaService.crearReserva(request);
    }

    // Listar y filtrar reservas
    @GetMapping
    public List<ReservaListadoResponse> listarReservas(
            @RequestParam(required = false) LocalDate fecha,
            @RequestParam(required = false) Long barberoId,
            @RequestParam(required = false) String estado) {
        return reservaService.listarReservas(fecha, barberoId, estado);
    }

    // Detalle de reserva
    @GetMapping("/{id}")
    public ReservaDetalleResponse obtenerDetalleReserva(
            @PathVariable Long id) {
        return reservaService.obtenerDetalleReserva(id);
    }

    // Cambiar estado de reserva
    @PatchMapping("/{id}/estado")
    public Reserva cambiarEstadoReserva(
            @PathVariable Long id,
            @Valid @RequestBody CambiarEstadoReservaRequest request) {
        return reservaService.cambiarEstadoReserva(
                id,
                request.getEstado());
    }
}