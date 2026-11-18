package com.barbersoft.barbersoft.repository;

import com.barbersoft.barbersoft.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

        List<Reserva> findByBarberoIdAndEstadoAndFechaInicioLessThanAndFechaFinGreaterThan(
                        Long barberoId,
                        String estado,
                        LocalDateTime fechaFin,
                        LocalDateTime fechaInicio);

        boolean existsByCodigoReserva(String codigoReserva);

        boolean existsByBarberoIdAndEstadoAndFechaInicioLessThanAndFechaFinGreaterThan(
                        Long barberoId,
                        String estado,
                        LocalDateTime fechaFin,
                        LocalDateTime fechaInicio);

        boolean existsByEstadoAndFechaInicioLessThanAndFechaFinGreaterThan(
                        String estado,
                        LocalDateTime fechaFin,
                        LocalDateTime fechaInicio);

        @Query("""
                            SELECT r
                            FROM Reserva r
                            WHERE (:barberoId IS NULL OR r.barbero.id = :barberoId)
                            AND (:estado IS NULL OR r.estado = :estado)
                            AND (cast(:desde as localdatetime) IS NULL OR r.fechaInicio >= :desde)
                            AND (cast(:hasta as localdatetime) IS NULL OR r.fechaFin < :hasta)
                            ORDER BY r.fechaInicio ASC
                        """)
        List<Reserva> buscarReservas(
                        @Param("barberoId") Long barberoId,
                        @Param("estado") String estado,
                        @Param("desde") LocalDateTime desde,
                        @Param("hasta") LocalDateTime hasta);

}