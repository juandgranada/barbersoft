package com.barbersoft.barbersoft.repository;

import com.barbersoft.barbersoft.entity.HorarioBarbero;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioBarberoRepository
        extends JpaRepository<HorarioBarbero, Long> {

    List<HorarioBarbero> findByBarberoIdAndDiaSemanaAndActivoTrue(
            Long barberoId,
            Short diaSemana
    );

}