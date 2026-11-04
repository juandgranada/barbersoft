package com.barbersoft.barbersoft.controller;

import com.barbersoft.barbersoft.dto.DisponibilidadRequest;
import com.barbersoft.barbersoft.dto.DisponibilidadResponse;
import com.barbersoft.barbersoft.service.DisponibilidadService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/disponibilidad")
public class DisponibilidadController {

    private final DisponibilidadService disponibilidadService;

    public DisponibilidadController(
            DisponibilidadService disponibilidadService) {
        this.disponibilidadService = disponibilidadService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public DisponibilidadResponse consultarDisponibilidad(
            @RequestBody DisponibilidadRequest request) {

        return disponibilidadService.consultarDisponibilidad(request);
    }
}