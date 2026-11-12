package com.barbersoft.barbersoft.repository;

import com.barbersoft.barbersoft.entity.Barbero;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarberoRepository extends JpaRepository<Barbero, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Barbero b WHERE b.id = :id")
    Optional<Barbero> findByIdForUpdate(@Param("id") Long id);
}