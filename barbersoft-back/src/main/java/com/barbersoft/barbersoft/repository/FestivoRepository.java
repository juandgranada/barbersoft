package com.barbersoft.barbersoft.repository;

import com.barbersoft.barbersoft.entity.Festivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FestivoRepository extends JpaRepository<Festivo, Long> {

    boolean existsByFechaAndActivoTrue(java.time.LocalDate fecha);
    boolean existsByFecha(LocalDate fecha);

    List<Festivo> findAllByOrderByFechaAsc();

    List<Festivo> findByFechaBetweenOrderByFechaAsc(
            java.time.LocalDate inicio,
            java.time.LocalDate fin
    );
}