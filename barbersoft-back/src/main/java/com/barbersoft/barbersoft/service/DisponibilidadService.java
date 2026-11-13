package com.barbersoft.barbersoft.service;

import com.barbersoft.barbersoft.dto.DisponibilidadRequest;
import com.barbersoft.barbersoft.dto.DisponibilidadResponse;
import com.barbersoft.barbersoft.repository.BarberoRepository;
import com.barbersoft.barbersoft.repository.BarberoServicioRepository;
import com.barbersoft.barbersoft.repository.HorarioBarberoRepository;
import com.barbersoft.barbersoft.repository.ReservaRepository;
import com.barbersoft.barbersoft.repository.ServicioRepository;
import com.barbersoft.barbersoft.repository.FestivoRepository;
import com.barbersoft.barbersoft.repository.ExcepcionHorarioRepository;
import com.barbersoft.barbersoft.entity.Barbero;
import com.barbersoft.barbersoft.entity.Servicio;
import com.barbersoft.barbersoft.entity.HorarioBarbero;
import com.barbersoft.barbersoft.entity.Reserva;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.time.DayOfWeek;

@Service
public class DisponibilidadService {

    private final BarberoRepository barberoRepository;
    private final BarberoServicioRepository barberoServicioRepository;
    private final HorarioBarberoRepository horarioBarberoRepository;
    private final ReservaRepository reservaRepository;
    private final ServicioRepository servicioRepository;
    private final FestivoRepository festivoRepository;
    private final ExcepcionHorarioRepository excepcionHorarioRepository;

    public DisponibilidadService(
            BarberoRepository barberoRepository,
            BarberoServicioRepository barberoServicioRepository,
            HorarioBarberoRepository horarioBarberoRepository,
            ReservaRepository reservaRepository,
            ServicioRepository servicioRepository,
            FestivoRepository festivoRepository,
            ExcepcionHorarioRepository excepcionHorarioRepository) {
        this.barberoRepository = barberoRepository;
        this.barberoServicioRepository = barberoServicioRepository;
        this.horarioBarberoRepository = horarioBarberoRepository;
        this.reservaRepository = reservaRepository;
        this.servicioRepository = servicioRepository;
        this.festivoRepository = festivoRepository;
        this.excepcionHorarioRepository = excepcionHorarioRepository;
    }

    public void validarSolicitud(DisponibilidadRequest request) {
        // Validar que todos los campos requeridos estén presentes y sean válidos
        if (request == null ||
                request.getBarberoId() == null ||
                request.getFecha() == null ||
                request.getServiciosIds() == null ||
                request.getServiciosIds().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar el barbero, la fecha y al menos un servicio");
        }
        // validar que la fecha a consultar no sea anterior a la fecha actual
        if (request.getFecha().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se pueden consultar fechas pasadas");
        }
        // Validar que no haya identificadores de servicios nulos o duplicados
        if (request.getServiciosIds().contains(null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La lista contiene identificadores de servicios inválidos");
        }
        // Validar que no haya servicios duplicados
        Set<Long> idsUnicos = new HashSet<>(request.getServiciosIds());
        if (idsUnicos.size() != request.getServiciosIds().size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede seleccionar el mismo servicio varias veces");
        }
        // Validar que el barbero exista y esté activo
        Barbero barbero = barberoRepository.findById(request.getBarberoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El barbero no existe"));
        if (!Boolean.TRUE.equals(barbero.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El barbero no se encuentra activo");
        }
        // Validar que todos los servicios existan, estén activos y sean ofrecidos por
        // el barbero
        List<Servicio> servicios = servicioRepository.findAllById(
                request.getServiciosIds());
        if (servicios.size() != request.getServiciosIds().size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Uno o varios servicios no existen");
        }
        for (Servicio servicio : servicios) {

            if (!Boolean.TRUE.equals(servicio.getActivo())) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Uno o varios servicios están inactivos");
            }

            boolean asignado = barberoServicioRepository
                    .existsByBarberoIdAndServicioIdAndActivoTrue(
                            request.getBarberoId(),
                            servicio.getId());

            if (!asignado) {

                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El barbero no ofrece uno o varios servicios seleccionados");
            }
        }
    }

    // Calcular la duración total de los servicios seleccionados
    private Integer calcularDuracionTotal(List<Long> serviciosIds) {
        List<Servicio> servicios = servicioRepository.findAllById(serviciosIds);
        int duracionTotal = 0;
        for (Servicio servicio : servicios) {
            duracionTotal += servicio.getDuracionMinutos();
        }
        return duracionTotal;
    }

    // Generar horarios de inicio cada 30 minutos dentro de una jornada
    private List<LocalTime> generarHorarios(
            LocalTime horaInicio,
            LocalTime horaFin,
            Integer duracionTotal) {

        List<LocalTime> horarios = new ArrayList<>();

        LocalTime horaActual = horaInicio;

        while (!horaActual.plusMinutes(duracionTotal).isAfter(horaFin)) {

            horarios.add(horaActual);

            // Avanzar 30 minutos para generar el siguiente horario
            horaActual = horaActual.plusMinutes(30);
        }

        return horarios;
    }

    // Obtener el número del día de la semana según nuestra base de datos
    private Short obtenerDiaSemana(LocalDate fecha) {

        DayOfWeek dia = fecha.getDayOfWeek();

        return (short) dia.getValue();
    }

    // Obtener los horarios activos del barbero para una fecha
    private List<HorarioBarbero> obtenerHorariosDelDia(
            Long barberoId,
            LocalDate fecha) {

        Short diaSemana = obtenerDiaSemana(fecha);

        return horarioBarberoRepository
                .findByBarberoIdAndDiaSemanaAndActivoTrue(
                        barberoId,
                        diaSemana);
    }

    // Generar todos los horarios disponibles dentro de las jornadas del barbero
    private List<LocalTime> generarHorariosDelBarbero(
            Long barberoId,
            LocalDate fecha,
            Integer duracionTotal) {

        List<LocalTime> horariosDisponibles = new ArrayList<>();

        // Consultar las jornadas registradas para el barbero
        List<HorarioBarbero> horariosDelDia = obtenerHorariosDelDia(barberoId, fecha);

        // Recorrer cada jornada del barbero
        for (HorarioBarbero horario : horariosDelDia) {

            List<LocalTime> horariosJornada = generarHorarios(
                    horario.getHoraInicio(),
                    horario.getHoraFin(),
                    duracionTotal);

            horariosDisponibles.addAll(horariosJornada);
        }

        return horariosDisponibles;
    }

    // Verificar si un horario se encuentra ocupado
    private boolean horarioEstaOcupado(
            Long barberoId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) {

        List<Reserva> reservas = reservaRepository
                .findByBarberoIdAndEstadoAndFechaInicioLessThanAndFechaFinGreaterThan(
                        barberoId,
                        "CONFIRMADA",
                        fechaFin,
                        fechaInicio);

        return !reservas.isEmpty();
    }

    // Eliminar los horarios que se cruzan con reservas existentes
    private List<LocalTime> filtrarHorariosOcupados(
            Long barberoId,
            LocalDate fecha,
            Integer duracionTotal,
            List<LocalTime> horariosGenerados) {

        List<LocalTime> horariosDisponibles = new ArrayList<>();

        for (LocalTime horaInicio : horariosGenerados) {

            LocalDateTime fechaInicio = LocalDateTime.of(fecha, horaInicio);

            LocalDateTime fechaFin = fechaInicio.plusMinutes(duracionTotal);

            boolean ocupado = horarioEstaOcupado(
                    barberoId,
                    fechaInicio,
                    fechaFin);

            if (!ocupado) {
                horariosDisponibles.add(horaInicio);
            }
        }

        return horariosDisponibles;
    }

    // Eliminar horarios anteriores a la hora actual
    private List<LocalTime> filtrarHorariosPasados(
            LocalDate fecha,
            List<LocalTime> horarios) {

        // Si la fecha consultada es futura, todos los horarios son válidos
        if (!fecha.equals(LocalDate.now())) {
            return horarios;
        }

        LocalTime horaActual = LocalTime.now();

        List<LocalTime> horariosValidos = new ArrayList<>();

        for (LocalTime horario : horarios) {

            if (horario.isAfter(horaActual)) {
                horariosValidos.add(horario);
            }
        }

        return horariosValidos;
    }

    // Obtener los horarios realmente disponibles
    private List<LocalTime> consultarHorariosDisponibles(
            Long barberoId,
            LocalDate fecha,
            Integer duracionTotal) {

        // 1. Si es festivo, toda la barbería estará cerrada
        if (festivoRepository.existsByFechaAndActivoTrue(fecha)) {
            return new ArrayList<>();
        }

        // 2. Si el barbero tiene una excepción activa, no estará disponible
        boolean tieneExcepcion = excepcionHorarioRepository
                .existsByBarberoIdAndActivoTrueAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
                        barberoId,
                        fecha,
                        fecha);

        if (tieneExcepcion) {
            return new ArrayList<>();
        }

        // 3. Generar horarios según las jornadas del barbero
        List<LocalTime> horariosGenerados = generarHorariosDelBarbero(barberoId, fecha, duracionTotal);

        // 4. Eliminar horarios ocupados
        List<LocalTime> horariosDisponibles = filtrarHorariosOcupados(
                barberoId,
                fecha,
                duracionTotal,
                horariosGenerados);

        // 5. Eliminar horarios pasados
        return filtrarHorariosPasados(fecha, horariosDisponibles);
    }

    // Consultar la disponibilidad de un barbero
    public DisponibilidadResponse consultarDisponibilidad(
            DisponibilidadRequest request) {

        // Validar la información recibida
        validarSolicitud(request);

        // Calcular la duración total de los servicios
        Integer duracionTotal = calcularDuracionTotal(
                request.getServiciosIds());

        // Consultar los horarios disponibles
        List<LocalTime> horarios = consultarHorariosDisponibles(
                request.getBarberoId(),
                request.getFecha(),
                duracionTotal);

        // Crear la respuesta
        DisponibilidadResponse response = new DisponibilidadResponse();

        response.setBarberoId(request.getBarberoId());
        response.setFecha(request.getFecha());
        response.setDuracionTotal(duracionTotal);

        // Convertir los horarios a texto
        List<String> horariosTexto = new ArrayList<>();

        for (LocalTime horario : horarios) {
            horariosTexto.add(horario.toString());
        }

        response.setHorariosDisponibles(horariosTexto);

        return response;
    }
}