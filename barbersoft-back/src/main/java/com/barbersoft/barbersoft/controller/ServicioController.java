package com.barbersoft.barbersoft.controller;

import com.barbersoft.barbersoft.entity.Servicio;
import com.barbersoft.barbersoft.service.ServicioService;
import com.barbersoft.barbersoft.dto.ResumenServiciosRequest;
import com.barbersoft.barbersoft.dto.ResumenServiciosResponse;
import com.barbersoft.barbersoft.dto.ServicioRequest;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "http://localhost:5173")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    // Listar todos los servicios, incluyendo los inactivos
    @GetMapping
    public List<Servicio> listarServicios() {
        return servicioService.listarServicios();
    }

    // Listar solamente los servicios activos
    @GetMapping("/activos")
    public List<Servicio> listarServiciosActivos() {
        return servicioService.listarServiciosActivos();
    }

    // Crear un nuevo servicio
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Servicio guardarServicio(
            @RequestBody ServicioRequest servicio) {
        return servicioService.guardarServicio(servicio);
    }

    // Buscar un servicio por su ID
    @GetMapping("/{id}")
    public Servicio buscarPorId(@PathVariable Long id) {
        return servicioService.buscarPorId(id);
    }

    // Actualizar servicio
    @PutMapping("/{id}")
    public Servicio actualizarServicio(
            @PathVariable Long id,
            @RequestBody ServicioRequest servicio) {

        return servicioService.actualizarServicio(id, servicio);
    }

    // Activar o desactivar
    @PatchMapping("/{id}/estado")
    public Servicio cambiarEstado(
            @PathVariable Long id,
            @RequestParam Boolean activo) {

        return servicioService.cambiarEstado(id, activo);
    }

    @PostMapping("/summary")
    public ResumenServiciosResponse calcularResumen(
            @RequestBody ResumenServiciosRequest request) {

        return servicioService.calcularResumen(request);
    }
}