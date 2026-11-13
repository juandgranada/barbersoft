package com.barbersoft.barbersoft.service;

import com.barbersoft.barbersoft.dto.HorarioBarberoRequest;
import com.barbersoft.barbersoft.entity.Barbero;
import com.barbersoft.barbersoft.entity.HorarioBarbero;
import com.barbersoft.barbersoft.repository.BarberoRepository;
import com.barbersoft.barbersoft.repository.HorarioBarberoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;

@Service
public class HorarioBarberoService {

    private final HorarioBarberoRepository horarioRepository;
    private final BarberoRepository barberoRepository;

    public HorarioBarberoService(
            HorarioBarberoRepository horarioRepository,
            BarberoRepository barberoRepository
    ) {
        this.horarioRepository = horarioRepository;
        this.barberoRepository = barberoRepository;
    }

    public List<HorarioBarbero> listarPorBarbero(Long barberoId) {
        if (!barberoRepository.existsById(barberoId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "El barbero no existe"
            );
        }

        return horarioRepository.findByBarberoIdOrderByDiaSemanaAscHoraInicioAsc(
                barberoId
        );
    }

    @Transactional
    public HorarioBarbero crear(HorarioBarberoRequest request) {

        Barbero barbero = obtenerBarberoActivo(request.getBarberoId());

        validarHorario(request, null);

        HorarioBarbero horario = new HorarioBarbero();
        horario.setBarbero(barbero);
        horario.setDiaSemana(request.getDiaSemana());
        horario.setHoraInicio(request.getHoraInicio());
        horario.setHoraFin(request.getHoraFin());
        horario.setActivo(
                request.getActivo() == null ? true : request.getActivo()
        );

        return horarioRepository.save(horario);
    }

    @Transactional
    public HorarioBarbero actualizar(
            Long id,
            HorarioBarberoRequest request
    ) {
        HorarioBarbero horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El horario no existe"
                ));

        Barbero barbero = obtenerBarberoActivo(request.getBarberoId());

        validarHorario(request, id);

        horario.setBarbero(barbero);
        horario.setDiaSemana(request.getDiaSemana());
        horario.setHoraInicio(request.getHoraInicio());
        horario.setHoraFin(request.getHoraFin());

        if (request.getActivo() != null) {
            horario.setActivo(request.getActivo());
        }

        return horarioRepository.save(horario);
    }

    @Transactional
    public HorarioBarbero cambiarEstado(Long id, Boolean activo) {

        HorarioBarbero horario = horarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El horario no existe"
                ));

        horario.setActivo(activo);
        return horarioRepository.save(horario);
    }

    private Barbero obtenerBarberoActivo(Long id) {

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Debe indicar el barbero"
            );
        }

        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El barbero no existe"
                ));

        if (!Boolean.TRUE.equals(barbero.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El barbero está inactivo"
            );
        }

        return barbero;
    }

    private void validarHorario(
            HorarioBarberoRequest request,
            Long idExcluir
    ) {

        if (request.getDiaSemana() == null
                || request.getDiaSemana() < 1
                || request.getDiaSemana() > 6) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo se pueden asignar horarios de lunes a sábado"
            );
        }

        LocalTime inicio = request.getHoraInicio();
        LocalTime fin = request.getHoraFin();

        if (inicio == null || fin == null || !fin.isAfter(inicio)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La hora final debe ser posterior a la hora inicial"
            );
        }

        boolean turnoManana =
                !inicio.isBefore(LocalTime.of(8, 0))
                && !fin.isAfter(LocalTime.of(12, 0));

        boolean turnoTarde =
                !inicio.isBefore(LocalTime.of(14, 0))
                && !fin.isAfter(LocalTime.of(17, 0));

        if (!turnoManana && !turnoTarde) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El horario debe estar dentro de 08:00-12:00 o 14:00-17:00"
            );
        }

        if (!Boolean.FALSE.equals(request.getActivo())
                && horarioRepository.existeHorarioSuperpuesto(
                        request.getBarberoId(),
                        request.getDiaSemana(),
                        inicio,
                        fin,
                        idExcluir
                )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El horario se superpone con otro turno activo del barbero"
            );
        }
    }
}