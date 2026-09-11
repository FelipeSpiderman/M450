package ch.tbz.bank.software;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 * Tests für die Hauptklasse {@link Main}.
 *
 * <p>Da {@code main(String[])} nicht per JUnit aufgerufen werden kann,
 * wird hier die eigentliche Logik in einer getrennten Methode
 * ({@link Main#run(int, String[])}) getestet. Diese Methode wird in
 * {@code main} aufgerufen, sobald die Argumente überprüft sind.
 */
class MainTest {

    @Nested
    @Test
    void run_gibtFehlerBeiFehlendenArgumenten() {
        assertThrows(ArgonException.class, () -> new Main().run(0, null));
    }

    @Nested
    @Test
    @DisplayName("Übergang EUR nach CHF")
    void run_uebergangEurNachChf_meldetErgebnis() {
        Main app = new Main();

        int result = app.run(1, new String[]{"EUR", "CHF"});

        assertEquals(0, result);
    }

    @Nested
    @Test
    @DisplayName("Übergang CHF nach EUR")
    void run_uebergangChfNachEur_meldetErgebnis() {
        Main app = new Main();

        int result = app.run(1, new String[]{"CHF", "EUR"});

        assertEquals(0, result);
    }

    // ArgException wird hier nur als Beispielausnahme demonstriert;
    // es muss nicht zwingend in Main existieren, solange run den Fall
    // nicht auslöst. Für konsistente Fehlerbehandlung empfiehlt sich
    // jedoch eine eigene Fehlerklasse in der Bank-Software.
    static class ArgException extends Exception {
        ArgException(String m) { super(m); }
    }
}
