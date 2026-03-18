package com.example.medallas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mapea la respuesta de GET /api/auth/me del backend principal.
 * Se ignoran campos desconocidos para mayor resiliencia.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsuarioResponseDto {

    private Long id;
    private String nombre;
    private String email;
    private String rol;
    private int puntosTotales;
}
