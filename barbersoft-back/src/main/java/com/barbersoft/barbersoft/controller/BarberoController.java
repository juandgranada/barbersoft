package com.barbersoft.barbersoft.controller;

import com.barbersoft.barbersoft.entity.Barbero;
import com.barbersoft.barbersoft.service.BarberoService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barberos")
@CrossOrigin (origins = "http://localhost:5173")
public class BarberoController {

    private final BarberoService barberoService;

    public BarberoController(BarberoService barberoService) {
        this.barberoService = barberoService;
    }

    @GetMapping
    public List<Barbero> listarBarberos() {
        return barberoService.listarBarberos();
    }

    @PostMapping
    public Barbero guardarBarbero(@RequestBody Barbero barbero) {
        return barberoService.guardarBarbero(barbero);
    }

    @GetMapping("/{id}")
    public Barbero buscarPorId(@PathVariable Long id) {
        return barberoService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Barbero actualizarBarbero(
            @PathVariable Long id,
            @RequestBody Barbero barbero) {

        return barberoService.actualizarBarbero(id, barbero);
    }

    @DeleteMapping("/{id}")
    public void eliminarBarbero(@PathVariable Long id) {
        barberoService.eliminarBarbero(id);
    }
}