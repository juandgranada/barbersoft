package com.barbersoft.barbersoft.service;

import com.barbersoft.barbersoft.entity.Servicio;
import com.barbersoft.barbersoft.repository.ServicioRepository;
import com.barbersoft.barbersoft.dto.ResumenServiciosRequest;
import com.barbersoft.barbersoft.dto.ResumenServiciosResponse;
import com.barbersoft.barbersoft.dto.ServicioRequest;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    // Listar todos los servicios, incluyendo los inactivos
    public List<Servicio> listarServicios() {
        return servicioRepository.findAll();
    }

    // Listar solo los servicios activos, ordenados por nombre
    public List<Servicio> listarServiciosActivos() {
        return servicioRepository.findByActivoTrueOrderByNombreAsc();
    }

    // Crear servicio
    @Transactional
    public Servicio guardarServicio(ServicioRequest request) {

        validarDatos(request);

        Servicio servicio = new Servicio();

        servicio.setNombre(request.getNombre().trim());
        servicio.setDescripcion(normalizarDescripcion(request.getDescripcion()));
        servicio.setPrecio(request.getPrecio());
        servicio.setDuracionMinutos(request.getDuracionMinutos());
        servicio.setActivo(true);

        return servicioRepository.save(servicio);
    }

    // Buscar servicio por ID
    public Servicio buscarPorId(Long id) {

        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar el ID del servicio");
        }

        return servicioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El servicio no existe"));
    }

    // Actualizar servicio
    @Transactional
    public Servicio actualizarServicio(Long id, ServicioRequest request) {

        validarDatos(request);

        Servicio servicio = buscarPorId(id);

        servicio.setNombre(request.getNombre().trim());
        servicio.setDescripcion(normalizarDescripcion(request.getDescripcion()));
        servicio.setPrecio(request.getPrecio());
        servicio.setDuracionMinutos(request.getDuracionMinutos());

        return servicioRepository.save(servicio);
    }

    // Activar o desactivar servicio
    @Transactional
    public Servicio cambiarEstado(Long id, Boolean activo) {

        if (activo == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe indicar el estado del servicio");
        }

        Servicio servicio = buscarPorId(id);

        servicio.setActivo(activo);

        return servicioRepository.save(servicio);
    }

    // Validar información del servicio
    private void validarDatos(ServicioRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar los datos del servicio");
        }

        if (request.getNombre() == null
                || request.getNombre().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre del servicio es obligatorio");
        }

        if (request.getNombre().trim().length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre no puede superar 100 caracteres");
        }

        if (request.getDescripcion() != null
                && request.getDescripcion().length() > 300) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La descripción no puede superar 300 caracteres");
        }

        if (request.getPrecio() == null
                || request.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio debe ser mayor que cero");
        }

        if (request.getPrecio().scale() > 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio solamente puede tener dos decimales");
        }

        if (request.getPrecio().precision() > 10) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio supera el valor máximo permitido");
        }

        if (request.getDuracionMinutos() == null
                || request.getDuracionMinutos() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La duración debe ser mayor que cero");
        }
    }

    private String normalizarDescripcion(String descripcion) {

        if (descripcion == null || descripcion.isBlank()) {
            return null;
        }

        return descripcion.trim();
    }

    // HU02: Calcular resumen de servicios seleccionados
    public ResumenServiciosResponse calcularResumen(
            ResumenServiciosRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar los servicios seleccionados");
        }

        List<Long> ids = request.getServiciosIds();

        if (ids == null || ids.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar al menos un servicio");
        }

        if (ids.contains(null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los IDs de servicios no pueden ser nulos");
        }

        Set<Long> idsUnicos = new HashSet<>(ids);

        if (idsUnicos.size() != ids.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No puede seleccionar el mismo servicio varias veces");
        }

        List<Servicio> servicios = servicioRepository.findAllById(ids);

        if (servicios.size() != ids.size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Uno o varios servicios no existen");
        }

        BigDecimal precioTotal = BigDecimal.ZERO;
        Integer duracionTotal = 0;

        for (Servicio servicio : servicios) {

            if (!Boolean.TRUE.equals(servicio.getActivo())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El servicio " + servicio.getNombre()
                                + " no está disponible");
            }

            precioTotal = precioTotal.add(servicio.getPrecio());
            duracionTotal += servicio.getDuracionMinutos();
        }

        return new ResumenServiciosResponse(
                servicios.size(),
                precioTotal,
                duracionTotal);
    }
}