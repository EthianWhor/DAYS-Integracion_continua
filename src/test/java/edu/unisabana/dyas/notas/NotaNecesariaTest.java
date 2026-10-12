package edu.unisabana.dyas.notas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NotaNecesariaTest {

    private final CalculadoraNotas calculadora = new CalculadoraNotas();

    @ParameterizedTest(name = "{0}, {1} -> necesita {2}")
    @CsvSource({
            "3.0, 3.0, 3.0",
            "2.0, 2.0, 4.5",
            "3.5, 2.8, 2.8",
            "5.0, 5.0, 0.0"
    })
    void calculaLaNotaNecesariaEnElTercerCorte(double c1, double c2, double esperada) {
        assertEquals(esperada, calculadora.notaNecesariaTercerCorte(c1, c2));
    }

    @Test
    void avisaCuandoYaNoEsPosibleAprobar() {
        assertThrows(IllegalStateException.class, () -> calculadora.notaNecesariaTercerCorte(1.0, 1.0));
    }
}
