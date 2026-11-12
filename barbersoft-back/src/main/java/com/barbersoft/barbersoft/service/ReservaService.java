package com.barbersoft.barbersoft.service;

import com.barbersoft.barbersoft.dto.CrearReservaRequest;
import com.barbersoft.barbersoft.dto.DisponibilidadRequest;
import com.barbersoft.barbersoft.dto.DisponibilidadResponse;
import com.barbersoft.barbersoft.dto.ReservaResponse;
import com.barbersoft.barbersoft.entity.Barbero;
import com.barbersoft.barbersoft.entity.Reserva;
import com.barbersoft.barbersoft.entity.ReservaServicio;
import com.barbersoft.barbersoft.entity.Servicio;
import com.barbersoft.barbersoft.repository.BarberoRepository;
import com.barbersoft.barbersoft.repository.ReservaRepository;
import com.barbersoft.barbersoft.repository.ReservaServicioRepository;
import com.barbersoft.barbersoft.repository.ServicioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ReservaServicioRepository reservaServicioRepository;
    private final BarberoRepository barberoRepository;
    private final ServicioRepository servicioRepository;
    private final DisponibilidadService disponibilidadService;

    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    public ReservaService(
            ReservaRepository reservaRepository,
            ReservaServicioRepository reservaServicioRepository,
            BarberoRepository barberoRepository,
            ServicioRepository servicioRepository,
            DisponibilidadService disponibilidadService
    ) {
        this.reservaRepository = reservaRepository;
        this.reservaServicioRepository = reservaServicioRepository;
        this.barberoRepository = barberoRepository;
        this.servicioRepository = servicioRepository;
        this.disponibilidadService = disponibilidadService;
    }

    @Transactional
    public ReservaResponse crearReserva(CrearReservaRequest request) {

        // 1. Bloquear el registro del barbero durante la creación
        Barbero barbero = barberoRepository.findByIdForUpdate(request.getBarberoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El barbero seleccionado no existe"
                ));

        // 2. Comprobar nuevamente la disponibilidad
        DisponibilidadRequest disponibilidadRequest = new DisponibilidadRequest();

        disponibilidadRequest.setBarberoId(request.getBarberoId());
        disponibilidadRequest.setFecha(request.getFecha());
        disponibilidadRequest.setServiciosIds(request.getServiciosIds());

        DisponibilidadResponse disponibilidad =
                disponibilidadService.consultarDisponibilidad(disponibilidadRequest);

        LocalDateTime fechaInicio = request.getFecha().atTime(request.getHoraInicio());

        // 3. Verificar que la hora seleccionada siga disponible
        if (!disponibilidad.getHorariosDisponibles()
                .contains(request.getHoraInicio().toString())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El horario seleccionado ya no está disponible. Seleccione otro horario"
            );
        }

        // 4. Obtener los servicios seleccionados
        List<Servicio> servicios = servicioRepository.findAllById(request.getServiciosIds());

        if (servicios.size() != request.getServiciosIds().size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Uno o varios servicios seleccionados no existen"
            );
        }

        Map<Long, Servicio> serviciosPorId = new HashMap<>();

        for (Servicio servicio : servicios) {
            serviciosPorId.put(servicio.getId(), servicio);
        }

        // 5. Calcular precio total y duración
        BigDecimal total = BigDecimal.ZERO;
        int duracionTotal = 0;

        for (Long servicioId : request.getServiciosIds()) {

            Servicio servicio = serviciosPorId.get(servicioId);

            total = total.add(servicio.getPrecio());
            duracionTotal += servicio.getDuracionMinutos();
        }

        LocalDateTime fechaFin = fechaInicio.plusMinutes(duracionTotal);

        // 6. Generar un código único para la reserva
        String codigoReserva = generarCodigoReserva();

        // 7. Crear la reserva principal
        Reserva reserva = new Reserva();

        reserva.setCodigoReserva(codigoReserva);
        reserva.setBarbero(barbero);
        reserva.setNombreCliente(request.getNombreCliente().trim());
        reserva.setTelefonoCliente(request.getTelefonoCliente().trim());
        reserva.setEmailCliente(request.getEmailCliente().trim());
        reserva.setObservaciones(request.getObservaciones());
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setTotal(total);
        reserva.setEstado("CONFIRMADA");

        Reserva reservaGuardada = reservaRepository.saveAndFlush(reserva);

        // 8. Registrar los servicios asociados a la reserva
        List<ReservaServicio> detalles = new ArrayList<>();

        for (Long servicioId : request.getServiciosIds()) {

            Servicio servicio = serviciosPorId.get(servicioId);

            ReservaServicio detalle = new ReservaServicio();

            detalle.setReserva(reservaGuardada);
            detalle.setServicio(servicio);

            // Guardamos los valores actuales como históricos
            detalle.setPrecio(servicio.getPrecio());
            detalle.setDuracionMinutos(servicio.getDuracionMinutos());

            detalles.add(detalle);
        }

        reservaServicioRepository.saveAll(detalles);

        // 9. Preparar respuesta para el frontend
        return new ReservaResponse(
                reservaGuardada.getCodigoReserva(),
                reservaGuardada.getEstado(),
                reservaGuardada.getNombreCliente(),
                reservaGuardada.getEmailCliente(),
                barbero.getId(),
                reservaGuardada.getFechaInicio(),
                reservaGuardada.getFechaFin(),
                reservaGuardada.getTotal(),
                new ArrayList<>(request.getServiciosIds())
        );
    }

    private String generarCodigoReserva() {

        String codigo;

        do {
            StringBuilder parteAleatoria = new StringBuilder();

            for (int i = 0; i < 8; i++) {
                int posicion = RANDOM.nextInt(CARACTERES.length());
                parteAleatoria.append(CARACTERES.charAt(posicion));
            }

            codigo = "BAR-" + parteAleatoria;

        } while (reservaRepository.existsByCodigoReserva(codigo));

        return codigo;
    }
}