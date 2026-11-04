package com.barbersoft.barbersoft.service;

import com.barbersoft.barbersoft.dto.AsignacionServicioRequest;
import com.barbersoft.barbersoft.entity.Barbero;
import com.barbersoft.barbersoft.entity.Servicio;
import com.barbersoft.barbersoft.entity.BarberoServicio;
import com.barbersoft.barbersoft.repository.BarberoRepository;
import com.barbersoft.barbersoft.repository.ServicioRepository;
import com.barbersoft.barbersoft.repository.BarberoServicioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AsignacionServicioService {

    private final BarberoRepository barberoRepository;
    private final ServicioRepository servicioRepository;
    private final BarberoServicioRepository barberoServicioRepository;

    public AsignacionServicioService(
            BarberoRepository barberoRepository,
            ServicioRepository servicioRepository,
            BarberoServicioRepository barberoServicioRepository) {

        this.barberoRepository = barberoRepository;
        this.servicioRepository = servicioRepository;
        this.barberoServicioRepository = barberoServicioRepository;
    }

    public BarberoServicio asignarServicio(AsignacionServicioRequest request) {

        if (request == null || request.getBarberoId() == null || request.getServicioId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe proporcionar el ID del barbero y del servicio"
            );
        }

        Barbero barbero = barberoRepository.findById(request.getBarberoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El barbero no existe"
                ));

        Servicio servicio = servicioRepository.findById(request.getServicioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "El servicio no existe"
                ));

        if (!Boolean.TRUE.equals(barbero.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El barbero está inactivo"
            );
        }

        if (!Boolean.TRUE.equals(servicio.getActivo())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El servicio está inactivo"
            );
        }

        boolean existeAsignacion = barberoServicioRepository
                .existsByBarberoIdAndServicioIdAndActivoTrue(
                        barbero.getId(),
                        servicio.getId()
                );

        if (existeAsignacion) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El servicio ya está asignado a este barbero"
            );
        }

        BarberoServicio asignacion = new BarberoServicio();
        asignacion.setBarbero(barbero);
        asignacion.setServicio(servicio);
        asignacion.setActivo(true);

        return barberoServicioRepository.save(asignacion);
    }
}