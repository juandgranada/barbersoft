package com.barbersoft.barbersoft.controller;

import com.barbersoft.barbersoft.dto.CrearReservaRequest;
import com.barbersoft.barbersoft.dto.ReservaResponse;
import com.barbersoft.barbersoft.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaResponse crearReserva(
            @Valid @RequestBody CrearReservaRequest request
    ) {
        return reservaService.crearReserva(request);
    }
}