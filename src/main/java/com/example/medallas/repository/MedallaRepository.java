package com.example.medallas.repository;

import com.example.medallas.entity.Medalla;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedallaRepository extends JpaRepository<Medalla, Long> {

    Optional<Medalla> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);
}
