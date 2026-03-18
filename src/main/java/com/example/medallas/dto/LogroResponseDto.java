package com.example.medallas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para la lista de logros del usuario autenticado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogroResponseDto {

    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaObtencion;
}
