package com.barbersoft.barbersoft.repository;

import com.barbersoft.barbersoft.entity.BarberoServicio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BarberoServicioRepository
        extends JpaRepository<BarberoServicio, Long> {

    List<BarberoServicio> findByBarberoIdAndActivoTrue(Long barberoId);

    boolean existsByBarberoIdAndServicioIdAndActivoTrue(
            Long barberoId,
            Long servicioId
    );
}