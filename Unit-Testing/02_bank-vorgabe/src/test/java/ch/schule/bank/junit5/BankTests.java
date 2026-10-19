package test.java.ch.schule.bank.junit5;

import main.java.ch.schule.Bank;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für die Bank-Klasse.
 */
public class BankTests {

    @Test
    public void testCreateAccounts() {
        Bank bank = new Bank();

        assertEquals("S-1000", bank.createSavingsAccount());
        assertEquals("Y-1001", bank.createPromoYouthSavingsAccount());
        assertEquals("P-1002", bank.createSalaryAccount(-1000));

        // Positive Kreditlimite ist ungültig
        assertNull(bank.createSalaryAccount(1000));
    }

    @Test
    public void testDepositAndWithdraw() {
        Bank bank = new Bank();
        String id = bank.createSavingsAccount();

        // Einzahlung und Auszahlung
        assertTrue(bank.deposit(id, 10, 500));
        assertTrue(bank.withdraw(id, 11, 200));
        assertEquals(300, bank.getBalance(id));

        // Zu viel abheben
        assertFalse(bank.withdraw(id, 12, 400));
        assertEquals(300, bank.getBalance(id));

        // Nicht existierendes Konto
        assertFalse(bank.deposit("S-9999", 13, 100));
        assertFalse(bank.withdraw("S-9999", 13, 100));
        assertEquals(0, bank.getBalance("S-9999"));
    }

    @Test
    public void testBankBalance() {
        Bank bank = new Bank();
        String acc1 = bank.createSavingsAccount();
        String acc2 = bank.createSavingsAccount();

        bank.deposit(acc1, 1, 1000);
        bank.deposit(acc2, 1, 2000);

        // Bank-Gesamtsaldo ist negativ, da Kundengelder Verbindlichkeiten sind
        assertEquals(-3000, bank.getBalance());
    }

    @Test
    public void testPrintAndRanking() {
        Bank bank = new Bank();
        String acc1 = bank.createSavingsAccount();
        String acc2 = bank.createSavingsAccount();

        bank.deposit(acc1, 1, 1000);
        bank.deposit(acc2, 2, 2000);

        // Auszüge und Top/Bottom Rankings drucken
        bank.print(acc1);
        bank.print(acc1, 1970, 1);
        bank.print("S-9999"); // unbekanntes Konto
        bank.printTop5();
        bank.printBottom5();
    }
}