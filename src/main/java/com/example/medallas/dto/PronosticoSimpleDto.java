package com.example.medallas.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mapea cada elemento de GET /api/pronosticos/pronosticos-usuario.
 * Solo necesitamos el id para saber si el usuario tiene al menos un pronostico (medalla Ludopata).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PronosticoSimpleDto {

    private Long id;
}
