package ch.tbz.rechner;

/**
 * Ein einfacher Taschenrechner mit den vier Grundoperationen.
 */
public class Calculator {

    /**
     * Addition zweier Zahlen.
     */
    public double add(double summand1, double summand2) {
        return summand1 + summand2;
    }

    /**
     * Subtraktion zweier Zahlen.
     */
    public double subtract(double minuend, double subtrahend) {
        return minuend - subtrahend;
    }

    /**
     * Multiplikation zweier Zahlen.
     */
    public double multiply(double faktor1, double faktor2) {
        return faktor1 * faktor2;
    }

    /**
     * Division zweier Zahlen.
     *
     * @throws ArithmeticException wenn durch null dividiert wird
     */
    public double divide(double zaehler, double nenner) {
        if (nenner == 0) {
            throw new ArithmeticException("Division durch null ist nicht erlaubt.");
        }
        return zaehler / nenner;
    }
}
