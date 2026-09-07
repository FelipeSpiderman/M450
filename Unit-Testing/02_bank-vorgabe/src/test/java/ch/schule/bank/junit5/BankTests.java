package ch.schule.bank.junit5;

import ch.schule.Bank;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für die Klasse Bank.
 */
public class BankTests {

    /**
     * Testet das Erstellen neuer Konten.
     */
    @Test
    public void testCreate() {
        Bank bank = new Bank();
        String id = bank.createSavingsAccount();
        assertEquals("S-1000", id);

        String promoId = bank.createPromoYouthSavingsAccount();
        assertEquals("Y-1001", promoId);

        String salaryId = bank.createSalaryAccount(-5000);
        assertEquals("P-1002", salaryId);
    }

    /**
     * Testet, dass eine ungültige Kreditlimite (positiv) kein Konto erstellt.
     */
    @Test
    public void testCreateSalaryMitPositivemLimit() {
        Bank bank = new Bank();
        assertNull(bank.createSalaryAccount(5000));
    }

    /**
     * Testet das Einzahlen auf ein Konto über die Bank.
     */
    @Test
    public void testDeposit() {
        Bank bank = new Bank();
        bank.createSavingsAccount();
        assertTrue(bank.deposit("S-1000", 13576, 12000));
        assertEquals(12000, bank.getBalance("S-1000"));
    }

    /**
     * Testet die Einzahlung auf ein nicht existierendes Konto.
     */
    @Test
    public void testDepositUnbekanntesKonto() {
        Bank bank = new Bank();
        assertFalse(bank.deposit("S-9999", 13576, 12000));
    }

    /**
     * Testet das Abheben von einem Konto über die Bank.
     */
    @Test
    public void testWithdraw() {
        Bank bank = new Bank();
        bank.createSavingsAccount();
        bank.deposit("S-1000", 13576, 12000);
        assertTrue(bank.withdraw("S-1000", 13577, 2000));
        assertEquals(10000, bank.getBalance("S-1000"));
    }

    /**
     * Testet, dass das Abheben ohne genügend Saldo fehlschlägt.
     */
    @Test
    public void testWithdrawZuViel() {
        Bank bank = new Bank();
        bank.createSavingsAccount();
        bank.deposit("S-1000", 13576, 12000);
        assertFalse(bank.withdraw("S-1000", 13577, 12001));
        assertEquals(12000, bank.getBalance("S-1000"));
    }

    /**
     * Testet, dass print() ohne Exception funktioniert (auch bei unbekanntem Konto).
     */
    @Test
    public void testPrint() {
        Bank bank = new Bank();
        bank.createSavingsAccount();
        bank.deposit("S-1000", 13576, 12000);
        bank.print("S-1000");
        bank.print("S-9999");
    }

    /**
     * Testet, dass print(year, month) ohne Exception funktioniert.
     */
    @Test
    public void testMonthlyPrint() {
        Bank bank = new Bank();
        bank.createSavingsAccount();
        bank.deposit("S-1000", 13576, 12000);
        bank.print("S-1000", 2008, 1);
        bank.print("S-9999", 2008, 1);
    }

    /**
     * Testet den Gesamtkontostand der Bank.
     * Die Bank betrachtet Einlagen auf Konten als Verbindlichkeit,
     * daher entspricht der Saldo der negativen Summe aller Kontokontostände.
     */
    @Test
    public void testBalance() {
        Bank bank = new Bank();
        bank.createSavingsAccount();
        bank.createSavingsAccount();
        bank.deposit("S-1000", 13576, 12000);
        bank.deposit("S-1001", 13576, 10000);
        assertEquals(22000, bank.getBalance("S-1000") + bank.getBalance("S-1001"));
        assertEquals(-22000, bank.getBalance());
    }

    /**
     * Testet den Kontostand für ein unbekanntes Konto (liefert 0).
     */
    @Test
    public void testBalanceUnbekanntesKonto() {
        Bank bank = new Bank();
        assertEquals(0, bank.getBalance("S-9999"));
    }

    /**
     * Testet, dass printTop5() keine Exception wirft und die
     * Sortierung funktioniert (höchstes Saldo zuerst).
     */
    @Test
    public void testTop5() {
        Bank bank = new Bank();
        bank.createSavingsAccount();
        bank.createSavingsAccount();
        bank.createSavingsAccount();
        bank.deposit("S-1000", 13576, 5000);
        bank.deposit("S-1001", 13576, 12000);
        bank.deposit("S-1002", 13576, 8000);
        bank.printTop5();
    }

    /**
     * Testet, dass printBottom5() keine Exception wirft und die
     * Sortierung funktioniert (tiefstes Saldo zuerst).
     */
    @Test
    public void testBottom5() {
        Bank bank = new Bank();
        bank.createSavingsAccount();
        bank.createSavingsAccount();
        bank.createSavingsAccount();
        bank.deposit("S-1000", 13576, 5000);
        bank.deposit("S-1001", 13576, 12000);
        bank.deposit("S-1002", 13576, 8000);
        bank.printBottom5();
    }
}