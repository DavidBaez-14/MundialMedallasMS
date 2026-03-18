package com.example.medallas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mapea cada elemento de la respuesta de GET /api/pronosticos/puntos del backend principal.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PuntosUsuarioDto {

    private Long partidoId;
    private String equipoLocal;
    private String equipoVisitante;
    private Integer golesLocalPredicho;
    private Integer golesVisitantePredicho;
    private Integer golesLocalReal;
    private Integer golesVisitanteReal;
    private Integer puntosObtenidos;
    private boolean partidoFinalizado;
}
