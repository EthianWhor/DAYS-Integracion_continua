package edu.unisabana.dyas.notas;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calcula la nota definitiva de una asignatura con tres cortes (30% - 30% - 40%).
 *
 * Reglas:
 * - Cada nota debe estar entre 0.0 y 5.0.
 * - La definitiva se redondea a una cifra decimal (mitad hacia arriba).
 * - Se aprueba con una definitiva mayor o igual a 3.0.
 */
public class CalculadoraNotas {

    static final BigDecimal NOTA_MINIMA = new BigDecimal("0.0");
    static final BigDecimal NOTA_MAXIMA = new BigDecimal("5.0");
    static final BigDecimal NOTA_APROBATORIA = new BigDecimal("3.0");

    private static final BigDecimal PESO_CORTE_1 = new BigDecimal("0.30");
    private static final BigDecimal PESO_CORTE_2 = new BigDecimal("0.30");
    private static final BigDecimal PESO_CORTE_3 = new BigDecimal("0.40");

    public double calcularDefinitiva(double corte1, double corte2, double corte3) {
        BigDecimal definitiva = validar(corte1, "corte 1").multiply(PESO_CORTE_1)
                .add(validar(corte2, "corte 2").multiply(PESO_CORTE_2))
                .add(validar(corte3, "corte 3").multiply(PESO_CORTE_3));
        // BigDecimal evita errores de punto flotante: con double, 3.05 puede quedar en 3.0499999...
        return definitiva.setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    public boolean aprueba(double definitiva) {
        return validar(definitiva, "definitiva").compareTo(NOTA_APROBATORIA) >= 0;
    }

    public double notaNecesariaTercerCorte(double corte1, double corte2) {
        return 0;
    }

    private BigDecimal validar(double nota, String nombre) {
        BigDecimal valor = BigDecimal.valueOf(nota);
        if (valor.compareTo(NOTA_MINIMA) < 0 || valor.compareTo(NOTA_MAXIMA) > 0) {
            throw new IllegalArgumentException(
                    "La nota del " + nombre + " debe estar entre 0.0 y 5.0, pero fue " + nota);
        }
        return valor;
    }
}
