package com.barbersoft.barbersoft.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.barbersoft.barbersoft.entity.BarberoServicio;

public interface BarberoServicioRepository
        extends JpaRepository<BarberoServicio, Long> {

    List<BarberoServicio> findByBarberoIdAndActivoTrue(Long barberoId);

    boolean existsByBarberoIdAndServicioIdAndActivoTrue(
            Long barberoId,
            Long servicioId
    );

    Optional<BarberoServicio> findFirstByBarberoIdAndActivoTrue(Long barberoId);

    void deleteByIdAndActivoTrue(Long id);

    Optional<BarberoServicio> findByBarberoIdAndServicioIdAndActivoTrue(
            Long barberoId,
            Long servicioId
    );
}