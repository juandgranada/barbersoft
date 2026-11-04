package com.barbersoft.barbersoft.repository;

import com.barbersoft.barbersoft.entity.ReservaServicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservaServicioRepository
        extends JpaRepository<ReservaServicio, Long> {

    List<ReservaServicio> findByReservaId(Long reservaId);

}