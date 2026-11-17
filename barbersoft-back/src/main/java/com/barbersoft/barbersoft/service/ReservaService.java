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
import com.barbersoft.barbersoft.dto.ReservaListadoResponse;
import com.barbersoft.barbersoft.dto.ReservaDetalleResponse;
import com.barbersoft.barbersoft.dto.ReservaServicioDetalleResponse;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            DisponibilidadService disponibilidadService) {
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
                        "El barbero seleccionado no existe"));

        // 2. Comprobar nuevamente la disponibilidad
        DisponibilidadRequest disponibilidadRequest = new DisponibilidadRequest();

        disponibilidadRequest.setBarberoId(request.getBarberoId());
        disponibilidadRequest.setFecha(request.getFecha());
        disponibilidadRequest.setServiciosIds(request.getServiciosIds());

        DisponibilidadResponse disponibilidad = disponibilidadService.consultarDisponibilidad(disponibilidadRequest);

        LocalDateTime fechaInicio = request.getFecha().atTime(request.getHoraInicio());

        // 3. Verificar que la hora seleccionada siga disponible
        if (!disponibilidad.getHorariosDisponibles()
                .contains(request.getHoraInicio().toString())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El horario seleccionado ya no está disponible. Seleccione otro horario");
        }

        // 4. Obtener los servicios seleccionados
        List<Servicio> servicios = servicioRepository.findAllById(request.getServiciosIds());

        if (servicios.size() != request.getServiciosIds().size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Uno o varios servicios seleccionados no existen");
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
                new ArrayList<>(request.getServiciosIds()));
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

    // ==========================================
    // HU07 - CONSULTA ADMINISTRATIVA DE RESERVAS
    // ==========================================

    @Transactional(readOnly = true)
    public List<ReservaListadoResponse> listarReservas(
            LocalDate fecha,
            Long barberoId,
            String estado) {

        if (barberoId != null && barberoId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El identificador del barbero no es válido");
        }

        if (estado != null) {
            estado = estado.trim().toUpperCase();

            if (!List.of("CONFIRMADA", "CANCELADA", "COMPLETADA")
                    .contains(estado)) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El estado de la reserva no es válido");
            }
        }

        LocalDateTime desde = fecha != null
                ? fecha.atStartOfDay()
                : null;

        LocalDateTime hasta = fecha != null
                ? fecha.plusDays(1).atStartOfDay()
                : null;

        List<Reserva> reservas = reservaRepository.buscarReservas(
                barberoId,
                estado,
                desde,
                hasta);

        return reservas.stream()
                .map(reserva -> new ReservaListadoResponse(
                        reserva.getId(),
                        reserva.getCodigoReserva(),
                        reserva.getNombreCliente(),
                        reserva.getBarbero().getNombre()
                                + " "
                                + reserva.getBarbero().getApellido(),
                        reserva.getFechaInicio(),
                        reserva.getFechaFin(),
                        reserva.getTotal(),
                        reserva.getEstado()))
                .collect(Collectors.toList());
    }

    // ==========================================
    // HU07 - DETALLE DE UNA RESERVA
    // ==========================================

    @Transactional(readOnly = true)
    public ReservaDetalleResponse obtenerDetalleReserva(Long id) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La reserva no existe"));

        List<ReservaServicioDetalleResponse> servicios = reservaServicioRepository.findByReservaId(id)
                .stream()
                .map(detalle -> new ReservaServicioDetalleResponse(
                        detalle.getServicio().getId(),
                        detalle.getServicio().getNombre(),
                        detalle.getPrecio(),
                        detalle.getDuracionMinutos()))
                .collect(Collectors.toList());

        return new ReservaDetalleResponse(
                reserva.getId(),
                reserva.getCodigoReserva(),
                reserva.getEstado(),
                reserva.getNombreCliente(),
                reserva.getTelefonoCliente(),
                reserva.getEmailCliente(),
                reserva.getBarbero().getId(),
                reserva.getBarbero().getNombre()
                        + " "
                        + reserva.getBarbero().getApellido(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getTotal(),
                reserva.getObservaciones(),
                servicios);
    }

    // ==========================================
    // HU07 - CAMBIAR ESTADO DE UNA RESERVA
    // ==========================================

    @Transactional
    public Reserva cambiarEstadoReserva(Long id, String nuevoEstado) {

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "La reserva no existe"));

        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar el nuevo estado");
        }

        nuevoEstado = nuevoEstado.trim().toUpperCase();

        if (!List.of("CONFIRMADA", "CANCELADA", "COMPLETADA")
                .contains(nuevoEstado)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado indicado no es válido");
        }

        String estadoActual = reserva.getEstado();

        if (estadoActual.equals(nuevoEstado)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La reserva ya tiene ese estado");
        }

        boolean transicionPermitida = estadoActual.equals("CONFIRMADA")
                && (nuevoEstado.equals("CANCELADA")
                        || nuevoEstado.equals("COMPLETADA"));

        if (!transicionPermitida) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se permite cambiar una reserva de "
                            + estadoActual + " a " + nuevoEstado);
        }

        reserva.setEstado(nuevoEstado);

        return reservaRepository.save(reserva);
    }

}