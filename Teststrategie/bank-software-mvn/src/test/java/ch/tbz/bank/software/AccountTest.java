package ch.tbz.bank.software;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 * Unit-Tests für die Klasse {@link Account}.
 *
 * <p>Deckung der Testbasis M450 (Abschnitt "Unit-Tests"):
 *   - Konstruktion (Sollwerte)
 *   - Guthaben-Anzeige (getBalance)
 *   - Ein- und Auszahlungen (deposit / withdraw)
 *   - Kontonummer (getId)
 *   - Währung (getCurrency)
 *   - Kontodaten (printAccountDetails)
 *   - Auszahlung unter der Grenze (Boundary)
 *   - Auszahlung über der Grenze (Fehlerfall)
 */
class AccountTest {

    // ------------------------------------------------------------------------------------------
    // 1. Konstruktion (Sollwerte)
    // ------------------------------------------------------------------------------------------

    @Test
    void Konstruktion_wertetSollwerteAus() {
        Account account = new Account(1, 1000.0, "CHF");
        assertEquals(1, account.getId());
        assertEquals(1000.0, account.getBalance());
        assertEquals("CHF", account.getCurrency());
    }

    @Test
    void Konstruktion_wertetGuthabenMitGanzzahlEins() {
        // 1000.0 == 1000 als Double
        Account account = new Account(1, 1000.0, "CHF");
        assertEquals(1000.0, account.getBalance());
    }

    // ------------------------------------------------------------------------------------------
    // 2. Guthaben-Anzeige (getBalance)
    // ------------------------------------------------------------------------------------------

    @Test
    void getBalance_gibtSollwertNachErstellungZurück() {
        Account account = new Account(1, 2500.0, "CHF");
        assertEquals(2500.0, account.getBalance());
    }

    @Test
    void getBalance_gibtGuthabenNachZahlungenZurück() {
        Account account = new Account(1, 1000.0, "CHF");
        account.deposit(500.0);
        account.withdraw(200.0);
        assertEquals(1300.0, account.getBalance());
    }

    // ------------------------------------------------------------------------------------------
    // 3. Ein- und Auszahlungen (deposit / withdraw)
    // ------------------------------------------------------------------------------------------

    @Test
    void deposit_erhöht_dasGuthaben() {
        Account account = new Account(1, 1000.0, "CHF");
        account.deposit(500.0);
        assertEquals(1500.0, account.getBalance());
    }

    @Test
    void deposit_beiMehrfachenZahlungen_kumuliert() {
        Account account = new Account(1, 100.0, "CHF");
        account.deposit(100.0);
        account.deposit(250.0);
        account.deposit(1000.0);
        assertEquals(1450.0, account.getBalance());
    }

    @Test
    void withdraw_verringert_dasGuthaben() {
        Account account = new Account(1, 1000.0, "CHF");
        account.withdraw(300.0);
        assertEquals(700.0, account.getBalance());
    }

    @Test
    void withdraw_beiGuthaben_wird_vollständig_eingebucht() {
        Account account = new Account(1, 1000.0, "CHF");
        account.withdraw(333.33);
        assertEquals(666.67, account.getBalance());
    }

    @Test
    void withdraw_beiNullBetrag_lasstGuthabenUnverändert() {
        Account account = new Account(1, 1000.0, "CHF");
        account.withdraw(0.0);
        assertEquals(1000.0, account.getBalance());
    }

    // ------------------------------------------------------------------------------------------
    // 4. Kontonummer (getId)
    // ------------------------------------------------------------------------------------------

    @Test
    void getId_gibt_dieBelegteKontonummerZurück() {
        Account account = new Account(42, 0.0, "CHF");
        assertEquals(42, account.getId());
    }

    // ------------------------------------------------------------------------------------------
    // 5. Währung (getCurrency)
    // ------------------------------------------------------------------------------------------

    @Test
    void getCurrency_gibt_dieBelegteWaehrungZurück() {
        Account account = new Account(1, 0.0, "EUR");
        assertEquals("EUR", account.getCurrency());
    }

    // ------------------------------------------------------------------------------------------
    // 6. Kontodaten (printAccountDetails)
    // ------------------------------------------------------------------------------------------

    @Test
    void printAccountDetails_drucktKontonummerUndWährung() {
        Account account = new Account(1, 1000.0, "CHF");
        String output = account.printAccountDetails();
        assertTrue(output.contains("1"), "Kontonummer fehlt: " + output);
        assertTrue(output.contains("CHF"), "Waehrung fehlt: " + output);
        assertTrue(output.contains("1000.0"), "Guthaben fehlt: " + output);
    }

    // ------------------------------------------------------------------------------------------
    // 7. Auszahlung unter der Grenze (Boundary)
    // ------------------------------------------------------------------------------------------

    @Test
    void withdraw_genauAmGuthaben_gelinkt() {
        Account account = new Account(1, 1000.0, "CHF");
        account.withdraw(1000.0);
        assertEquals(0.0, account.getBalance());
    }

    @Test
    void withdraw_kleinerBetrag_gelinkt() {
        Account account = new Account(1, 1000.0, "CHF");
        account.withdraw(1.0);
        assertEquals(999.0, account.getBalance());
    }

    // ------------------------------------------------------------------------------------------
    // 8. Auszahlung über der Grenze (Fehlerfall)
    // ------------------------------------------------------------------------------------------

    @Test
    void withdraw_oberhalbGuthaben_wirftAusnahme() {
        Account account = new Account(1, 1000.0, "CHF");
        assertThrows(Exception.class, () -> account.withdraw(1500.0));
        // Guthaben bleibt unverändert, weil die Auszahlung abgelehnt wurde
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void withdraw_geradeOberhalbGuthaben_wirftAusnahme() {
        Account account = new Account(1, 1000.0, "CHF");
        assertThrows(Exception.class, () -> account.withdraw(1000.01));
        assertEquals(1000.0, account.getBalance());
    }

    @Test
    void deposit_negativerBetrag_wirftAusnahme() {
        Account account = new Account(1, 1000.0, "CHF");
        assertThrows(Exception.class, () -> account.deposit(-100.0));
        assertEquals(1000.0, account.getBalance());
    }
}
