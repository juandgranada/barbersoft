package com.barbersoft.barbersoft.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.barbersoft.barbersoft.dto.AsignacionServicioRequest;
import com.barbersoft.barbersoft.entity.BarberoServicio;
import com.barbersoft.barbersoft.service.AsignacionServicioService;

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

    @GetMapping("/barbero/{barberoId}")
    public ResponseEntity<java.util.List<BarberoServicio>> listarPorBarbero(
            @PathVariable Long barberoId) {

        return ResponseEntity.ok(
                asignacionServicioService.listarPorBarbero(barberoId)
        );
    }

    @DeleteMapping("/barbero/{barberoId}/servicio/{servicioId}")
    public ResponseEntity<Void> desasignarServicio(
            @PathVariable Long barberoId,
            @PathVariable Long servicioId) {

        asignacionServicioService.desasignarServicio(barberoId, servicioId);
        return ResponseEntity.noContent().build();
    }
}