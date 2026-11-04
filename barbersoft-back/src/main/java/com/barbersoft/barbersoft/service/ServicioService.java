package com.barbersoft.barbersoft.service;

import com.barbersoft.barbersoft.entity.Servicio;
import com.barbersoft.barbersoft.repository.ServicioRepository;
import com.barbersoft.barbersoft.dto.ResumenServiciosRequest;
import com.barbersoft.barbersoft.dto.ResumenServiciosResponse;

import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    public List<Servicio> listarServicios() {
        return servicioRepository.findAll();
    }

    public Servicio guardarServicio(Servicio servicio) {
        return servicioRepository.save(servicio);
    }

    public Servicio buscarPorId(Long id) {
        return servicioRepository.findById(id)
                .orElse(null);
    }

    public Servicio actualizarServicio(Long id, Servicio datos) {
        Servicio servicio = servicioRepository.findById(id)
                .orElse(null);
        if (servicio == null) {
            return null;
        }
        servicio.setNombre(datos.getNombre());
        servicio.setDescripcion(datos.getDescripcion());
        servicio.setPrecio(datos.getPrecio());
        servicio.setDuracionMinutos(datos.getDuracionMinutos());
        servicio.setActivo(datos.getActivo());
        return servicioRepository.save(servicio);
    }

    public void eliminarServicio(Long id) {
        servicioRepository.deleteById(id);
    }

    public ResumenServiciosResponse calcularResumen(
            ResumenServiciosRequest request) {
        // 1. Obtener los IDs enviados
        List<Long> ids = request.getServiciosIds();
        // 2. Validar que la lista no esté vacía
        if (ids == null || ids.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Debe seleccionar al menos un servicio");
        }
        // 3. Validar que no existan IDs nulos
        if (ids.contains(null)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los IDs de servicios no pueden ser nulos");
        }
        // 4. Evitar que se repita un mismo servicio
        Set<Long> idsUnicos = new HashSet<>(ids);
        if (idsUnicos.size() != ids.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No puede seleccionar el mismo servicio varias veces");
        }
        // 5. Consultar los servicios en la base de datos
        List<Servicio> servicios = servicioRepository.findAllById(ids);
        // 6. Comprobar que todos los servicios existan
        if (servicios.size() != ids.size()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Uno o varios servicios no existen");
        }
        // 7. Inicializar los acumuladores
        BigDecimal precioTotal = BigDecimal.ZERO;
        Integer duracionTotal = 0;
        // 8. Recorrer los servicios encontrados
        for (Servicio servicio : servicios) {
            // Comprobar que el servicio esté activo
            if (!Boolean.TRUE.equals(servicio.getActivo())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El servicio " + servicio.getNombre()
                                + " no está disponible");
            }
            // Sumar precio y duración
            precioTotal = precioTotal.add(servicio.getPrecio());
            duracionTotal += servicio.getDuracionMinutos();
        }
        // 9. Construir y devolver el resumen
        return new ResumenServiciosResponse(
                servicios.size(),
                precioTotal,
                duracionTotal);
    }
}