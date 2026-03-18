package com.example.medallas.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medallas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medalla {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(name = "requisito_tipo", length = 50)
    private String requisitoTipo;

    @Column(name = "requisito_valor")
    private Integer requisitoValor;
}
