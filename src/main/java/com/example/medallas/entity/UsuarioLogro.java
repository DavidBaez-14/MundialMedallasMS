package com.example.medallas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "usuario_logros",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "medalla_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioLogro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medalla_id", nullable = false)
    private Medalla medalla;

    @Column(name = "fecha_obtencion", nullable = false)
    private LocalDateTime fechaObtencion;
}
