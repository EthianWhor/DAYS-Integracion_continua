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

    /**
     * Nota mínima que se necesita en el tercer corte para aprobar, redondeada hacia arriba a una décima.
     *
     * @throws IllegalStateException si se necesitaría más de 5.0 (ya no es posible aprobar)
     */
    public double notaNecesariaTercerCorte(double corte1, double corte2) {
        BigDecimal faltante = NOTA_APROBATORIA
                .subtract(validar(corte1, "corte 1").multiply(PESO_CORTE_1))
                .subtract(validar(corte2, "corte 2").multiply(PESO_CORTE_2));
        // Hacia arriba: con 2.775 se necesita 2.8; con 2.7 no alcanzaría para aprobar.
        BigDecimal necesaria = faltante.divide(PESO_CORTE_3, 1, RoundingMode.CEILING);
        if (necesaria.compareTo(NOTA_MAXIMA) > 0) {
            throw new IllegalStateException(
                    "Ya no es posible aprobar: se necesitaría " + necesaria + " en el tercer corte");
        }
        return necesaria.max(NOTA_MINIMA).doubleValue();
    }

    public String concepto(double definitiva) {
        BigDecimal nota = validar(definitiva, "definitiva");
        if (nota.compareTo(new BigDecimal("4.5")) >= 0) {
            return "Excelente";
        } else if (nota.compareTo(new BigDecimal("4.0")) >= 0) {
            return "Sobresaliente";
        } else if (nota.compareTo(NOTA_APROBATORIA) >= 0) {
            return "Aprobado";
        }
        return "Reprobado";
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
