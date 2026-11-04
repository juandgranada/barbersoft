package com.barbersoft.barbersoft.controller;

import com.barbersoft.barbersoft.dto.HorarioBarberoRequest;
import com.barbersoft.barbersoft.entity.HorarioBarbero;
import com.barbersoft.barbersoft.service.HorarioBarberoService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/horario-barbero")
public class HorarioBarberoController {

    private final HorarioBarberoService horarioBarberoService;

    public HorarioBarberoController(
            HorarioBarberoService horarioBarberoService) {
        this.horarioBarberoService = horarioBarberoService;
    }

    @PostMapping
    public ResponseEntity<HorarioBarbero> registrarHorario(
            @RequestBody HorarioBarberoRequest request) {

        HorarioBarbero horario =
                horarioBarberoService.registrarHorario(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(horario);
    }
}