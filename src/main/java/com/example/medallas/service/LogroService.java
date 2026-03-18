package com.example.medallas.service;

import com.example.medallas.client.PrincipalApiClient;
import com.example.medallas.dto.LogroResponseDto;
import com.example.medallas.dto.PronosticoSimpleDto;
import com.example.medallas.dto.PuntosUsuarioDto;
import com.example.medallas.dto.UsuarioResponseDto;
import com.example.medallas.entity.Medalla;
import com.example.medallas.entity.UsuarioLogro;
import com.example.medallas.repository.MedallaRepository;
import com.example.medallas.repository.UsuarioLogroRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LogroService {

    // Codigos de medallas — deben coincidir con los sembrados en MedallaInitializer
    private static final String LUDOPATA = "LUDOPATA";
    private static final String ORACULO  = "ORACULO";
    private static final String AURA     = "AURA";
    private static final String TIBIO    = "TIBIO";
    private static final String HAMPA    = "HAMPA";
    private static final String BORRE    = "BORRE";

    private final PrincipalApiClient apiClient;
    private final MedallaRepository medallaRepository;
    private final UsuarioLogroRepository usuarioLogroRepository;

    public LogroService(PrincipalApiClient apiClient,
                        MedallaRepository medallaRepository,
                        UsuarioLogroRepository usuarioLogroRepository) {
        this.apiClient = apiClient;
        this.medallaRepository = medallaRepository;
        this.usuarioLogroRepository = usuarioLogroRepository;
    }

    /**
     * Valida el token del usuario contra el backend principal, evalua todas las
     * condiciones de medallas y persiste los nuevos logros obtenidos.
     *
     * @param authHeader Header "Authorization: Basic ..." reenviado desde el frontend.
     * @return Lista de LogroResponseDto con todas las medallas ya ganadas por el usuario.
     */
    public List<LogroResponseDto> obtenerMisLogros(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Se requiere autenticacion (Authorization header)");
        }

        // 1. Validar token y obtener datos del usuario del backend principal
        UsuarioResponseDto usuario = validarYObtenerUsuario(authHeader);

        // 2. Obtener datos necesarios del backend principal
        List<PuntosUsuarioDto> puntosList  = apiClient.obtenerPuntosUsuario(authHeader);
        List<PronosticoSimpleDto> pronosticos = apiClient.obtenerPronosticosUsuario(authHeader);

        // 3. Evaluar y persistir medallas
        evaluarYPersistirMedalla(LUDOPATA, usuario.getId(), condicionLudopata(pronosticos));
        evaluarYPersistirMedalla(ORACULO,  usuario.getId(), condicionOraculo(puntosList));
        evaluarYPersistirMedalla(AURA,     usuario.getId(), condicionAura(usuario));
        evaluarYPersistirMedalla(HAMPA,    usuario.getId(), condicionHampa(usuario));
        evaluarYPersistirMedalla(TIBIO,    usuario.getId(), condicionTibio(puntosList));
        evaluarYPersistirMedalla(BORRE,    usuario.getId(), condicionBorre(puntosList));

        // 4. Devolver todos los logros actuales del usuario
        return usuarioLogroRepository.findByUsuarioId(usuario.getId())
            .stream()
            .map(this::mapearLogro)
            .toList();
    }

    /**
     * Endpoint auxiliar para pruebas de despliegue.
     * Devuelve el catalogo de medallas registrado en base de datos.
     */
    public List<Medalla> obtenerCatalogoMedallas() {
        return medallaRepository.findAll();
    }

    // ---------------------------------------------------------------
    // Condiciones de cada medalla
    // ---------------------------------------------------------------

    /**
     * LUDOPATA: el usuario tiene al menos 1 pronostico (partido finalizado o no).
     */
    private boolean condicionLudopata(List<PronosticoSimpleDto> pronosticos) {
        return !pronosticos.isEmpty();
    }

    /**
     * ORACULO: al menos un pronostico tiene exactamente 5 puntos (marcador exacto).
     */
    private boolean condicionOraculo(List<PuntosUsuarioDto> puntosList) {
        return puntosList.stream()
            .anyMatch(p -> p.getPuntosObtenidos() != null && p.getPuntosObtenidos() == 5);
    }

    /**
     * AURA: la suma total de puntos acumulados por el usuario es >= 15.
     * Solo aplica a partidos finalizados.
     */
    private boolean condicionAura(UsuarioResponseDto usuario) {
        return usuario.getPuntosTotales() >= 15;
    }

    /**
     * HAMPA: la suma total de puntos acumulados por el usuario es >= 20.
     * Usamos puntosTotales del objeto usuario (calculado por el backend principal).
     */
    private boolean condicionHampa(UsuarioResponseDto usuario) {
        return usuario.getPuntosTotales() >= 20;
    }

    /**
     * TIBIO: acerto un empate. El resultado real es empatado Y el usuario obtuvo >= 3 puntos
     * (el sistema otorga 3 pts por acertar resultado aunque no sea exacto, y 5 por exacto).
     * Solo aplica a partidos finalizados.
     */
    private boolean condicionTibio(List<PuntosUsuarioDto> puntosList) {
        return puntosList.stream()
            .filter(PuntosUsuarioDto::isPartidoFinalizado)
            .anyMatch(p ->
                p.getGolesLocalReal() != null && p.getGolesVisitanteReal() != null
                && p.getGolesLocalReal().equals(p.getGolesVisitanteReal())  // empate real
                && p.getPuntosObtenidos() != null && p.getPuntosObtenidos() >= 3  // acerto resultado
            );
    }

    /**
     * BORRE: tiene 5 o mas pronosticos con 0 puntos en partidos ya finalizados.
     */
    private boolean condicionBorre(List<PuntosUsuarioDto> puntosList) {
        long fallos = puntosList.stream()
            .filter(PuntosUsuarioDto::isPartidoFinalizado)
            .filter(p -> p.getPuntosObtenidos() != null && p.getPuntosObtenidos() == 0)
            .count();
        return fallos >= 5;
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private UsuarioResponseDto validarYObtenerUsuario(String authHeader) {
        try {
            UsuarioResponseDto usuario = apiClient.obtenerUsuarioActual(authHeader);
            if (usuario == null || usuario.getId() == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido o usuario no encontrado");
            }
            return usuario;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se pudo validar el usuario: " + e.getMessage());
        }
    }

    /**
     * Si la condicion se cumple y el usuario aun no tiene esa medalla, la persiste.
     */
    private void evaluarYPersistirMedalla(String codigoMedalla, Long usuarioId, boolean condicion) {
        if (!condicion) {
            return;
        }
        if (usuarioLogroRepository.existsByUsuarioIdAndMedalla_Codigo(usuarioId, codigoMedalla)) {
            return; // ya la tiene
        }
        Medalla medalla = medallaRepository.findByCodigo(codigoMedalla)
            .orElseThrow(() -> new IllegalStateException(
                "Medalla con codigo '" + codigoMedalla + "' no encontrada. Verifica la inicializacion de datos."
            ));

        UsuarioLogro logro = new UsuarioLogro();
        logro.setUsuarioId(usuarioId);
        logro.setMedalla(medalla);
        logro.setFechaObtencion(LocalDateTime.now());

        usuarioLogroRepository.save(logro);
    }

    private LogroResponseDto mapearLogro(UsuarioLogro logro) {
        Medalla m = logro.getMedalla();
        return new LogroResponseDto(
            logro.getId(),
            m.getCodigo(),
            m.getNombre(),
            m.getDescripcion(),
            logro.getFechaObtencion()
        );
    }
}
