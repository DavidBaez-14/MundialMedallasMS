package com.example.medallas.controller;

import com.example.medallas.dto.LogroResponseDto;
import com.example.medallas.entity.Medalla;
import com.example.medallas.service.LogroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logros")
public class LogroController {

    private final LogroService logroService;

    public LogroController(LogroService logroService) {
        this.logroService = logroService;
    }

    /**
     * Devuelve las medallas ganadas por el usuario autenticado.
     *
     * El frontend debe enviar el mismo header de autenticacion que usa con el
     * backend principal: Authorization: Basic <base64(email:password)>
     *
     * Este endpoint:
     *  1. Valida el token contra /api/auth/me del backend principal.
     *  2. Evalua todas las condiciones de medallas en tiempo real.
     *  3. Persiste los nuevos logros que se hayan desbloqueado.
     *  4. Retorna la lista completa de medallas obtenidas.
     *
     * @param authHeader  Header "Authorization" reenviado por el frontend.
     * @return 200 con la lista de logros, o 401 si el token no es valido.
     */
    @GetMapping("/mis-medallas")
    public ResponseEntity<List<LogroResponseDto>> getMisMedallas(
            @RequestHeader("Authorization") String authHeader) {

        List<LogroResponseDto> logros = logroService.obtenerMisLogros(authHeader);
        return ResponseEntity.ok(logros);
    }

    /**
     * Lista el catalogo de medallas disponible en la tabla medallas.
     */
    @GetMapping("/medallas")
    public ResponseEntity<List<Medalla>> getCatalogoMedallas() {
        return ResponseEntity.ok(logroService.obtenerCatalogoMedallas());
    }
}
