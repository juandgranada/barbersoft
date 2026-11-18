package com.barbersoft.barbersoft.controller;

import com.barbersoft.barbersoft.dto.FestivoRequest;
import com.barbersoft.barbersoft.entity.Festivo;
import com.barbersoft.barbersoft.service.FestivoService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/festivos")
public class FestivoController {

    private final FestivoService service;

    public FestivoController(FestivoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Festivo> listar() {
        return service.listar();
    }

    @GetMapping("/anio/{anio}")
    public List<Festivo> listarPorAnio(@PathVariable Integer anio) {
        return service.listarPorAnio(anio);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Festivo crear(@RequestBody FestivoRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public Festivo actualizar(
            @PathVariable Long id,
            @RequestBody FestivoRequest request
    ) {
        return service.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public Festivo cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean activo
    ) {
        return service.cambiarEstado(id, activo);
    }
}