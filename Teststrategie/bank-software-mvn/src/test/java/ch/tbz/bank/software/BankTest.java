package ch.tbz.bank.software;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 * Unit-Tests für die Klasse {@link Bank}.
 *
 * <p>Deckung der Testbasis M450 (Abschnitt "Unit-Tests"):
 *   - Erstellen von Konten (createAccount)
 *   - Kontostände ansagen (getBalance / getById)
 *   - Auszahlungen von Konten (withdraw)
 *   - Kontoschlüssigkeit (getAccounts)
 *   - Kontonummer-Prüfung (getAccount)
 *   - Kontonummer nicht gefunden (Fehlerfall)
 */
class BankTest {

    // ------------------------------------------------------------------------------------------
    // 1. Erstellen von Konten (createAccount)
    // ------------------------------------------------------------------------------------------

    @Test
    void createAccount_erstelltKonto_mitSollwerten() {
        Bank bank = new Bank();
        Account account = bank.createAccount(1, 1000.0, "CHF");

        assertNotNull(account, "Konto darf nicht null sein");
        assertEquals(1, account.getId());
        assertEquals(1000.0, account.getBalance());
        assertEquals("CHF", account.getCurrency());
    }

    @Test
    void createAccount_meldeDoppelteId() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        assertThrows(Exception.class, () -> bank.createAccount(1, 2000.0, "CHF"));
    }

    // ------------------------------------------------------------------------------------------
    // 2. Kontostände ansagen (getBalance / getById)
    // ------------------------------------------------------------------------------------------

    @Test
    void getBalance_gibtSollwertZurück() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        assertEquals(1000.0, bank.getBalance(1));
    }

    @Test
    void getBalance_gibtSollwertNachZahlungZurück() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        bank.withdraw(300.0, 1);
        assertEquals(700.0, bank.getBalance(1));
    }

    @Test
    void getById_gibtKontoZurück() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        Account account = bank.getById(1);
        assertNotNull(account);
        assertEquals(1, account.getId());
    }

    @Test
    void getById_gibtNullBeiNichtVorhandenerId() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        assertNull(bank.getById(99));
    }

    // ------------------------------------------------------------------------------------------
    // 3. Auszahlungen von Konten (withdraw)
    // ------------------------------------------------------------------------------------------

    @Test
    void withdraw_verringertGuthaben_vonBankAnsicht() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        bank.withdraw(250.0, 1);
        assertEquals(750.0, bank.getBalance(1));
    }

    @Test
    void withdraw_oberhalbGuthaben_wirftAusnahme() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        assertThrows(Exception.class, () -> bank.withdraw(1500.0, 1));
    }

    @Test
    void withdraw_beiNichtVorhandenerId_wirftAusnahme() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        assertThrows(Exception.class, () -> bank.withdraw(100.0, 99));
    }

    // ------------------------------------------------------------------------------------------
    // 4. Kontoschlüssigkeit (getAccounts)
    // ------------------------------------------------------------------------------------------

    @Test
    void getAccounts_gibtAlleKontenInErstellungsreihenfolge() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        bank.createAccount(2, 2000.0, "EUR");
        bank.createAccount(3, 3000.0, "CHF");

        assertEquals(3, bank.getAccounts().size());
        assertEquals(1, bank.getAccounts().get(0).getId());
        assertEquals(2, bank.getAccounts().get(1).getId());
        assertEquals(3, bank.getAccounts().get(2).getId());
    }

    // ------------------------------------------------------------------------------------------
    // 5. Kontonummer-Prüfung (getAccount)
    // ------------------------------------------------------------------------------------------

    @Test
    void getAccount_gibtKontoBeiVorhandenerId() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        Account account = bank.getAccount(1);
        assertNotNull(account);
        assertEquals(1, account.getId());
    }

    @Test
    void getAccount_gibtNullBeiNichtVorhandenerId() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        assertNull(bank.getAccount(99));
    }

    // ------------------------------------------------------------------------------------------
    // 6. Kontonummer nicht gefunden (Fehlerfall)
    // ------------------------------------------------------------------------------------------

    @Test
    void getBalance_beiNichtVorhandenerId_gibtNull() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        assertNull(bank.getBalance(99));
    }

    @Test
    void getAccount_beiNichtVorhandenerId_wirftAusnahme() {
        Bank bank = new Bank();
        bank.createAccount(1, 1000.0, "CHF");
        assertThrows(Exception.class, () -> bank.getAccount(99));
    }
}
