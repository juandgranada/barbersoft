package com.barbersoft.barbersoft.service;

import com.barbersoft.barbersoft.dto.HorarioBarberoRequest;
import com.barbersoft.barbersoft.entity.Barbero;
import com.barbersoft.barbersoft.entity.HorarioBarbero;
import com.barbersoft.barbersoft.repository.BarberoRepository;
import com.barbersoft.barbersoft.repository.HorarioBarberoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;

@Service
public class HorarioBarberoService {

    private final BarberoRepository barberoRepository;
    private final HorarioBarberoRepository horarioBarberoRepository;

    public HorarioBarberoService(
            BarberoRepository barberoRepository,
            HorarioBarberoRepository horarioBarberoRepository) {

        this.barberoRepository = barberoRepository;
        this.horarioBarberoRepository = horarioBarberoRepository;
    }

    public HorarioBarbero registrarHorario(HorarioBarberoRequest request) {

        if (request == null ||
                request.getBarberoId() == null ||
                request.getDiaSemana() == null ||
                request.getHoraInicio() == null ||
                request.getHoraFin() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar todos los datos del horario"
            );
        }

        if (request.getDiaSemana() < 1 || request.getDiaSemana() > 7) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El día debe estar entre 1 y 7"
            );
        }

        if (!request.getHoraInicio().isBefore(request.getHoraFin())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora de inicio debe ser anterior a la hora de finalización"
            );
        }

        Barbero barbero = barberoRepository.findById(request.getBarberoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El barbero no existe"
                ));

        if (!Boolean.TRUE.equals(barbero.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El barbero está inactivo"
            );
        }

        List<HorarioBarbero> horarios =
                horarioBarberoRepository.findByBarberoIdAndDiaSemanaAndActivoTrue(
                        request.getBarberoId(),
                        request.getDiaSemana()
                );

        for (HorarioBarbero horario : horarios) {

            boolean seCruzan =
                    request.getHoraInicio().isBefore(horario.getHoraFin()) &&
                    request.getHoraFin().isAfter(horario.getHoraInicio());

            if (seCruzan) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El horario se cruza con otro horario registrado"
                );
            }
        }

        HorarioBarbero nuevoHorario = new HorarioBarbero();

        nuevoHorario.setBarbero(barbero);
        nuevoHorario.setDiaSemana(request.getDiaSemana());
        nuevoHorario.setHoraInicio(request.getHoraInicio());
        nuevoHorario.setHoraFin(request.getHoraFin());
        nuevoHorario.setActivo(true);

        return horarioBarberoRepository.save(nuevoHorario);
    }
}