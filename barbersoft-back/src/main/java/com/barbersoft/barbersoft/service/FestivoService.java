package com.barbersoft.barbersoft.service;

import com.barbersoft.barbersoft.dto.FestivoRequest;
import com.barbersoft.barbersoft.entity.Festivo;
import com.barbersoft.barbersoft.repository.FestivoRepository;
import com.barbersoft.barbersoft.repository.ReservaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FestivoService {

    private final FestivoRepository festivoRepository;
    private final ReservaRepository reservaRepository;

    public FestivoService(
            FestivoRepository festivoRepository,
            ReservaRepository reservaRepository
    ) {
        this.festivoRepository = festivoRepository;
        this.reservaRepository = reservaRepository;
    }

    public List<Festivo> listar() {
        return festivoRepository.findAllByOrderByFechaAsc();
    }

    public List<Festivo> listarPorAnio(Integer anio) {

        if (anio == null || anio < 2020 || anio > 2100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "El año no es válido"
            );
        }

        return festivoRepository.findByFechaBetweenOrderByFechaAsc(
                LocalDate.of(anio, 1, 1),
                LocalDate.of(anio, 12, 31)
        );
    }

    @Transactional
    public Festivo crear(FestivoRequest request) {

        validar(request);

        if (festivoRepository.existsByFecha(request.getFecha())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ya existe un registro para esa fecha. Si está inactivo, puede reactivarlo."
            );
        }

        // No permitimos registrar un festivo que afecte reservas confirmadas.
        LocalDateTime inicio = request.getFecha().atStartOfDay();
        LocalDateTime fin = request.getFecha().plusDays(1).atStartOfDay();

        boolean tieneReservas =
                reservaRepository.existsByEstadoAndFechaInicioLessThanAndFechaFinGreaterThan(
                        "CONFIRMADA",
                        fin,
                        inicio
                );

        if (tieneReservas) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede registrar el festivo porque existen reservas confirmadas para esa fecha"
            );
        }

        Festivo festivo = new Festivo();
        festivo.setNombre(request.getNombre().trim());
        festivo.setFecha(request.getFecha());
        festivo.setActivo(true);

        return festivoRepository.save(festivo);
    }

    @Transactional
    public Festivo actualizar(Long id, FestivoRequest request) {

        validar(request);

        Festivo festivo = festivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El festivo no existe"
                ));

        if (festivoRepository.existsByFechaAndActivoTrue(request.getFecha())
                && !festivo.getFecha().equals(request.getFecha())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ya existe un festivo activo para esa fecha"
            );
        }

        if (!festivo.getFecha().equals(request.getFecha())) {

            LocalDateTime inicio = request.getFecha().atStartOfDay();
            LocalDateTime fin = request.getFecha().plusDays(1).atStartOfDay();

            boolean tieneReservas =
                    reservaRepository.existsByEstadoAndFechaInicioLessThanAndFechaFinGreaterThan(
                            "CONFIRMADA", fin, inicio
                    );

            if (tieneReservas) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La nueva fecha tiene reservas confirmadas"
                );
            }
        }

        festivo.setNombre(request.getNombre().trim());
        festivo.setFecha(request.getFecha());

        return festivoRepository.save(festivo);
    }

    @Transactional
    public Festivo cambiarEstado(Long id, Boolean activo) {

        Festivo festivo = festivoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El festivo no existe"
                ));

        festivo.setActivo(activo);
        return festivoRepository.save(festivo);
    }

    private void validar(FestivoRequest request) {

        if (request == null
                || request.getNombre() == null
                || request.getNombre().isBlank()
                || request.getFecha() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre y la fecha del festivo son obligatorios"
            );
        }

        if (request.getNombre().length() > 120) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre no puede superar 120 caracteres"
            );
        }
    }
}