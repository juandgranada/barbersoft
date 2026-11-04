package com.barbersoft.barbersoft.controller;

import com.barbersoft.barbersoft.entity.Servicio;
import com.barbersoft.barbersoft.service.ServicioService;
import com.barbersoft.barbersoft.dto.ResumenServiciosRequest;
import com.barbersoft.barbersoft.dto.ResumenServiciosResponse;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@CrossOrigin(origins = "http://localhost:5173")
public class ServicioController {

    private final ServicioService servicioService;

    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    @GetMapping
    public List<Servicio> listarServicios() {
        return servicioService.listarServicios();
    }

    @PostMapping
    public Servicio guardarServicio(@RequestBody Servicio servicio) {
        return servicioService.guardarServicio(servicio);
    }

    @GetMapping("/{id}")
    public Servicio buscarPorId(@PathVariable Long id) {
        return servicioService.buscarPorId(id);
    }

    @PutMapping("/{id}")
    public Servicio actualizarServicio(
            @PathVariable Long id,
            @RequestBody Servicio servicio) {

        return servicioService.actualizarServicio(id, servicio);
    }

    @DeleteMapping("/{id}")
    public void eliminarServicio(@PathVariable Long id) {
        servicioService.eliminarServicio(id);
    }

    @PostMapping("/summary")
    public ResumenServiciosResponse calcularResumen(
            @RequestBody ResumenServiciosRequest request) {

        return servicioService.calcularResumen(request);
    }
}