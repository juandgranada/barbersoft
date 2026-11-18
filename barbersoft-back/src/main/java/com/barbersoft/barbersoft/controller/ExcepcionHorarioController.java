package com.barbersoft.barbersoft.controller;

import com.barbersoft.barbersoft.dto.ExcepcionHorarioRequest;
import com.barbersoft.barbersoft.entity.ExcepcionHorario;
import com.barbersoft.barbersoft.service.ExcepcionHorarioService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/excepciones-horario")
public class ExcepcionHorarioController {

    private final ExcepcionHorarioService service;

    public ExcepcionHorarioController(ExcepcionHorarioService service) {
        this.service = service;
    }

    @GetMapping("/barbero/{barberoId}")
    public List<ExcepcionHorario> listar(@PathVariable Long barberoId) {
        return service.listar(barberoId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExcepcionHorario crear(
            @RequestBody ExcepcionHorarioRequest request
    ) {
        return service.crear(request);
    }

    @PatchMapping("/{id}/estado")
    public ExcepcionHorario cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean activo
    ) {
        return service.cambiarEstado(id, activo);
    }
}