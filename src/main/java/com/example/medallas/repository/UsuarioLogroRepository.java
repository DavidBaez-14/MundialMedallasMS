package com.example.medallas.repository;

import com.example.medallas.entity.UsuarioLogro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioLogroRepository extends JpaRepository<UsuarioLogro, Long> {

    List<UsuarioLogro> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndMedalla_Codigo(Long usuarioId, String codigoMedalla);
}
