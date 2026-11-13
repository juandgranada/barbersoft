package com.barbersoft.barbersoft.controller;

import com.barbersoft.barbersoft.dto.HorarioBarberoRequest;
import com.barbersoft.barbersoft.entity.HorarioBarbero;
import com.barbersoft.barbersoft.service.HorarioBarberoService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horario-barbero")
public class HorarioBarberoController {

    private final HorarioBarberoService service;

    public HorarioBarberoController(HorarioBarberoService service) {
        this.service = service;
    }

    @GetMapping("/barbero/{barberoId}")
    public List<HorarioBarbero> listar(@PathVariable Long barberoId) {
        return service.listarPorBarbero(barberoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HorarioBarbero crear(
            @RequestBody HorarioBarberoRequest request
    ) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public HorarioBarbero actualizar(
            @PathVariable Long id,
            @RequestBody HorarioBarberoRequest request
    ) {
        return service.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public HorarioBarbero cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean activo
    ) {
        return service.cambiarEstado(id, activo);
    }
}