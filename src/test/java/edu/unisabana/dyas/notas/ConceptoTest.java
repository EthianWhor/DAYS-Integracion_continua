package edu.unisabana.dyas.notas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class ConceptoTest {

    private final CalculadoraNotas calculadora = new CalculadoraNotas();

    // Se prueban los dos lados de cada frontera (4.5, 4.0 y 3.0) y los extremos del rango.
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "5.0, Excelente",
            "4.5, Excelente",
            "4.4, Sobresaliente",
            "4.0, Sobresaliente",
            "3.9, Aprobado",
            "3.0, Aprobado",
            "2.9, Reprobado",
            "0.0, Reprobado"
    })
    void asignaElConceptoSegunLaDefinitiva(double definitiva, String esperado) {
        assertEquals(esperado, calculadora.concepto(definitiva));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 5.1})
    void rechazaDefinitivasFueraDeRango(double definitiva) {
        assertThrows(IllegalArgumentException.class, () -> calculadora.concepto(definitiva));
    }
}
