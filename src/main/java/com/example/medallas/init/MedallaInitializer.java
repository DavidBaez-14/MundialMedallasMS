package com.example.medallas.init;

import com.example.medallas.entity.Medalla;
import com.example.medallas.repository.MedallaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Siembra la tabla 'medallas' en el arranque.
 * Si la medalla ya existe por codigo, actualiza su metadata; si no existe, la crea.
 */
@Component
public class MedallaInitializer implements CommandLineRunner {

    private final MedallaRepository medallaRepository;

    public MedallaInitializer(MedallaRepository medallaRepository) {
        this.medallaRepository = medallaRepository;
    }

    @Override
    public void run(String... args) {
        List<Medalla> medallas = List.of(
            new Medalla(null,
                "LUDOPATA",
                "Ludopata",
                "Realizaste al menos un pronostico en el torneo.",
                "MIN_PRONOSTICOS",
                1),
            new Medalla(null,
                "ORACULO",
                "Oraculo",
                "Acertaste el marcador exacto de un partido.",
                "MARCADOR_EXACTO",
                1),
            new Medalla(null,
                "AURA",
                "Aura",
                "Acumulaste 15 puntos o mas en total.",
                "PUNTOS_TOTALES",
                15),
            new Medalla(null,
                "TIBIO",
                "Tibio",
                "Acertaste el resultado de un empate.",
                "EMPATE_ACERTADO",
                1),
            new Medalla(null,
                "HAMPA",
                "Hampa",
                "Acumulaste 20 puntos o mas en total.",
                "PUNTOS_TOTALES",
                20),
            new Medalla(null,
                "BORRE",
                "Borre",
                "Tienes 5 o mas pronosticos con 0 puntos en partidos finalizados.",
                "FALLOS_TOTALES",
                5)
        );

        for (Medalla medalla : medallas) {
            medallaRepository.findByCodigo(medalla.getCodigo())
                .ifPresentOrElse(existente -> {
                    existente.setNombre(medalla.getNombre());
                    existente.setDescripcion(medalla.getDescripcion());
                    existente.setRequisitoTipo(medalla.getRequisitoTipo());
                    existente.setRequisitoValor(medalla.getRequisitoValor());
                    medallaRepository.save(existente);
                    System.out.println("[MS-Medallas] Medalla actualizada: " + medalla.getCodigo());
                }, () -> {
                    medallaRepository.save(medalla);
                    System.out.println("[MS-Medallas] Medalla sembrada: " + medalla.getCodigo());
                });
        }

        System.out.println("[MS-Medallas] Inicializacion de medallas completada.");
    }
}
