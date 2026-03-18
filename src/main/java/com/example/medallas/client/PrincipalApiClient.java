package com.example.medallas.client;

import com.example.medallas.dto.PronosticoSimpleDto;
import com.example.medallas.dto.PuntosUsuarioDto;
import com.example.medallas.dto.UsuarioResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Cliente HTTP que consume los endpoints del backend principal MundialScore.
 * Reenvía el header Authorization: Basic para autenticación delegada.
 */
@Component
public class PrincipalApiClient {

    private final RestTemplate restTemplate;

    @Value("${principal.api.url}")
    private String principalApiUrl;

    public PrincipalApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Llama a GET /api/auth/me para validar el token y obtener los datos del usuario.
     *
     * @param authHeader El header "Authorization: Basic ..." tal como llega del frontend.
     * @return UsuarioResponseDto si el token es valido, lanza excepcion en caso contrario.
     * @throws HttpClientErrorException si el backend principal devuelve 401.
     */
    public UsuarioResponseDto obtenerUsuarioActual(String authHeader) {
        HttpHeaders headers = buildHeaders(authHeader);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UsuarioResponseDto> response = restTemplate.exchange(
            principalApiUrl + "/auth/me",
            HttpMethod.GET,
            entity,
            UsuarioResponseDto.class
        );

        return response.getBody();
    }

    /**
     * Llama a GET /api/pronosticos/puntos para obtener todos los pronosticos con puntuacion.
     * Devuelve lista con datos de partidos finalizados e incompletos.
     */
    public List<PuntosUsuarioDto> obtenerPuntosUsuario(String authHeader) {
        HttpHeaders headers = buildHeaders(authHeader);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<PuntosUsuarioDto>> response = restTemplate.exchange(
            principalApiUrl + "/pronosticos/puntos",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<PuntosUsuarioDto>>() {}
        );

        return response.getBody() != null ? response.getBody() : List.of();
    }

    /**
     * Llama a GET /api/pronosticos/pronosticos-usuario para obtener todos los pronosticos
     * del usuario (incluyendo partidos no finalizados)
     * Usado para la medalla Ludopata.
     */
    public List<PronosticoSimpleDto> obtenerPronosticosUsuario(String authHeader) {
        HttpHeaders headers = buildHeaders(authHeader);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<PronosticoSimpleDto>> response = restTemplate.exchange(
            principalApiUrl + "/pronosticos/pronosticos-usuario",
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<List<PronosticoSimpleDto>>() {}
        );

        return response.getBody() != null ? response.getBody() : List.of();
    }

    private HttpHeaders buildHeaders(String authHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
