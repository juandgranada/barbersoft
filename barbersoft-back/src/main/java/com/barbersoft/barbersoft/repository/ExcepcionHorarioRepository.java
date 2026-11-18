package com.barbersoft.barbersoft.repository;

import com.barbersoft.barbersoft.entity.ExcepcionHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ExcepcionHorarioRepository
        extends JpaRepository<ExcepcionHorario, Long> {

    List<ExcepcionHorario> findByBarberoIdOrderByFechaInicioAsc(Long barberoId);

    boolean existsByBarberoIdAndActivoTrueAndFechaInicioLessThanEqualAndFechaFinGreaterThanEqual(
            Long barberoId,
            LocalDate fecha,
            LocalDate fecha2
    );

    @Query("""
        SELECT COUNT(e) > 0
        FROM ExcepcionHorario e
        WHERE e.barbero.id = :barberoId
          AND e.activo = true
          AND e.fechaInicio <= :fechaFin
          AND e.fechaFin >= :fechaInicio
    """)
    boolean existeExcepcionSuperpuesta(
            @Param("barberoId") Long barberoId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}