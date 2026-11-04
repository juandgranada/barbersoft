package com.barbersoft.barbersoft.controller;

import com.barbersoft.barbersoft.dto.AsignacionServicioRequest;
import com.barbersoft.barbersoft.entity.BarberoServicio;
import com.barbersoft.barbersoft.service.AsignacionServicioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignments")
public class AsignacionServicioController {

    private final AsignacionServicioService asignacionServicioService;

    public AsignacionServicioController(
            AsignacionServicioService asignacionServicioService) {
        this.asignacionServicioService = asignacionServicioService;
    }

    @PostMapping
    public ResponseEntity<BarberoServicio> asignarServicio(
            @RequestBody AsignacionServicioRequest request) {

        BarberoServicio asignacion =
                asignacionServicioService.asignarServicio(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(asignacion);
    }
}