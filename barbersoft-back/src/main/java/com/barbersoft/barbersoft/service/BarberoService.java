package com.barbersoft.barbersoft.service;

import com.barbersoft.barbersoft.entity.Barbero;
import com.barbersoft.barbersoft.repository.BarberoRepository;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BarberoService {
    private final BarberoRepository barberoRepository;

    public BarberoService(BarberoRepository barberoRepository) {
        this.barberoRepository = barberoRepository;
    }

    public List<Barbero> listarBarberos() {
        return barberoRepository.findAll();
    }

    public Barbero guardarBarbero(Barbero barbero) {
        return barberoRepository.save(barbero);
    }

    public Barbero buscarPorId(Long id) {
        return barberoRepository.findById(id)
                .orElse(null);
    }

    public Barbero actualizarBarbero(Long id, Barbero datos) {
        Barbero barbero = barberoRepository.findById(id)
                .orElse(null);
        if (barbero == null) {
            return null;
        }
        barbero.setNombre(datos.getNombre());
        barbero.setApellido(datos.getApellido());
        barbero.setTelefono(datos.getTelefono());
        barbero.setEmail(datos.getEmail());
        barbero.setFoto(datos.getFoto());
        barbero.setDescripcion(datos.getDescripcion());
        barbero.setActivo(datos.getActivo());
        return barberoRepository.save(barbero);
    }

    public void eliminarBarbero(Long id) {
        barberoRepository.deleteById(id);
    }
}
