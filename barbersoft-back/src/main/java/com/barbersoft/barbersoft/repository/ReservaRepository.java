package com.barbersoft.barbersoft.repository;

import com.barbersoft.barbersoft.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByBarberoIdAndEstadoAndFechaInicioLessThanAndFechaFinGreaterThan(
            Long barberoId,
            String estado,
            LocalDateTime fechaFin,
            LocalDateTime fechaInicio
    );

    boolean existsByCodigoReserva(String codigoReserva);
    
}