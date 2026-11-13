package com.barbersoft.barbersoft.service;

import com.barbersoft.barbersoft.dto.ExcepcionHorarioRequest;
import com.barbersoft.barbersoft.entity.Barbero;
import com.barbersoft.barbersoft.entity.ExcepcionHorario;
import com.barbersoft.barbersoft.repository.BarberoRepository;
import com.barbersoft.barbersoft.repository.ExcepcionHorarioRepository;
import com.barbersoft.barbersoft.repository.ReservaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class ExcepcionHorarioService {

    private static final Set<String> MOTIVOS =
            Set.of("VACACIONES", "PERMISO", "DIA_LIBRE");

    private final ExcepcionHorarioRepository excepcionRepository;
    private final BarberoRepository barberoRepository;
    private final ReservaRepository reservaRepository;

    public ExcepcionHorarioService(
            ExcepcionHorarioRepository excepcionRepository,
            BarberoRepository barberoRepository,
            ReservaRepository reservaRepository
    ) {
        this.excepcionRepository = excepcionRepository;
        this.barberoRepository = barberoRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<ExcepcionHorario> listar(Long barberoId) {

        if (!barberoRepository.existsById(barberoId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "El barbero no existe"
            );
        }

        return excepcionRepository.findByBarberoIdOrderByFechaInicioAsc(
                barberoId
        );
    }

    @Transactional
    public ExcepcionHorario crear(ExcepcionHorarioRequest request) {

        validarDatos(request);

        Barbero barbero = barberoRepository.findByIdForUpdate(
                request.getBarberoId()
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "El barbero no existe"
        ));

        if (!Boolean.TRUE.equals(barbero.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El barbero está inactivo"
            );
        }

        if (excepcionRepository.existeExcepcionSuperpuesta(
                barbero.getId(),
                request.getFechaInicio(),
                request.getFechaFin()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El barbero ya tiene una ausencia registrada en ese período"
            );
        }

        LocalDateTime inicio =
                request.getFechaInicio().atStartOfDay();

        LocalDateTime fin =
                request.getFechaFin().plusDays(1).atStartOfDay();

        boolean tieneReservas =
                reservaRepository
                        .existsByBarberoIdAndEstadoAndFechaInicioLessThanAndFechaFinGreaterThan(
                                barbero.getId(),
                                "CONFIRMADA",
                                fin,
                                inicio
                        );

        if (tieneReservas) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede registrar la ausencia porque el barbero tiene reservas confirmadas en ese período"
            );
        }

        ExcepcionHorario excepcion = new ExcepcionHorario();
        excepcion.setBarbero(barbero);
        excepcion.setFechaInicio(request.getFechaInicio());
        excepcion.setFechaFin(request.getFechaFin());
        excepcion.setMotivo(request.getMotivo());
        excepcion.setDescripcion(request.getDescripcion());
        excepcion.setActivo(true);

        return excepcionRepository.save(excepcion);
    }

    @Transactional
    public ExcepcionHorario cambiarEstado(Long id, Boolean activo) {

        ExcepcionHorario excepcion = excepcionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "La ausencia no existe"
                ));

        excepcion.setActivo(activo);
        return excepcionRepository.save(excepcion);
    }

    private void validarDatos(ExcepcionHorarioRequest request) {

        if (request == null
                || request.getBarberoId() == null
                || request.getFechaInicio() == null
                || request.getFechaFin() == null
                || request.getMotivo() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar barbero, fechas y motivo"
            );
        }

        if (request.getFechaInicio().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha inicial no puede ser anterior a hoy"
            );
        }

        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La fecha final no puede ser anterior a la inicial"
            );
        }

        if (!MOTIVOS.contains(request.getMotivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El motivo debe ser VACACIONES, PERMISO o DIA_LIBRE"
            );
        }

        if (request.getDescripcion() != null
                && request.getDescripcion().length() > 300) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripción no puede superar 300 caracteres"
            );
        }
    }
}