package ch.tbz.rechner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit-Tests für die Klasse Calculator.
 */
class CalculatorTest {

    private Calculator calculator;

    /**
     * Wird vor jedem Test ausgeführt – stellt einen frischen Rechner bereit.
     */
    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @Test
    @DisplayName("Addition von zwei positiven Zahlen")
    void testAddPositiv() {
        assertEquals(5.0, calculator.add(2.0, 3.0), 0.0001);
    }

    @Test
    @DisplayName("Addition mit negativer Zahl")
    void testAddNegativ() {
        assertEquals(-1.0, calculator.add(2.0, -3.0), 0.0001);
    }

    @Test
    @DisplayName("Subtraktion zweier positiver Zahlen")
    void testSubtract() {
        assertEquals(4.0, calculator.subtract(10.0, 6.0), 0.0001);
    }

    @Test
    @DisplayName("Subtraktion ergibt negatives Resultat")
    void testSubtractNegativ() {
        assertEquals(-2.0, calculator.subtract(4.0, 6.0), 0.0001);
    }

    @Test
    @DisplayName("Multiplikation zweier Zahlen")
    void testMultiply() {
        assertEquals(15.0, calculator.multiply(3.0, 5.0), 0.0001);
    }

    @Test
    @DisplayName("Multiplikation mit null")
    void testMultiplyNull() {
        assertEquals(0.0, calculator.multiply(8.0, 0.0), 0.0001);
    }

    @Test
    @DisplayName("Division zweier Zahlen")
    void testDivide() {
        assertEquals(2.5, calculator.divide(5.0, 2.0), 0.0001);
    }

    @Test
    @DisplayName("Division durch null wirft Exception")
    void testDivideDurchNull() {
        assertThrows(ArithmeticException.class,
                () -> calculator.divide(5.0, 0.0));
    }

    /**
     * Parameterisierter Test: läuft automatisch mit mehreren
     * Ein-/Ausgabepaaren für die Addition.
     */
    @ParameterizedTest
    @CsvSource({
            "1, 1, 2",
            "2, 3, 5",
            "-3, 3, 0",
            "10, 0, 10"
    })
    void testAddParameterized(double a, double b, double expected) {
        assertEquals(expected, calculator.add(a, b), 0.0001);
    }
}
