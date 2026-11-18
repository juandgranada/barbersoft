package com.barbersoft.barbersoft.repository;

import com.barbersoft.barbersoft.entity.HorarioBarbero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;

public interface HorarioBarberoRepository
                extends JpaRepository<HorarioBarbero, Long> {

        List<HorarioBarbero> findByBarberoIdAndDiaSemanaAndActivoTrue(
                        Long barberoId,
                        Short diaSemana);

        List<HorarioBarbero> findByBarberoIdOrderByDiaSemanaAscHoraInicioAsc(
                        Long barberoId);

        @Query("""
                            SELECT COUNT(h) > 0
                            FROM HorarioBarbero h
                            WHERE h.barbero.id = :barberoId
                              AND h.diaSemana = :dia
                              AND h.activo = true
                              AND h.horaInicio < :horaFin
                              AND h.horaFin > :horaInicio
                              AND (:idExcluir IS NULL OR h.id <> :idExcluir)
                        """)
        boolean existeHorarioSuperpuesto(
                        @Param("barberoId") Long barberoId,
                        @Param("dia") Short dia,
                        @Param("horaInicio") LocalTime horaInicio,
                        @Param("horaFin") LocalTime horaFin,
                        @Param("idExcluir") Long idExcluir);

}