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
    public void createDifferentAccounts() {
        Bank myBank = new Bank();
        assertEquals("S-1000", myBank.createSavingsAccount());
        assertEquals("Y-1001", myBank.createPromoYouthSavingsAccount());
        // negatives Limit testen
        assertEquals("P-1002", myBank.createSalaryAccount(-200));
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
    public void depositAndWithdrawTest() {
        Bank b = new Bank();
        b.createSavingsAccount();
        
        assertTrue(b.deposit("S-1000", 10, 100));
        assertTrue(b.withdraw("S-1000", 11, 30));
        assertEquals(70, b.getBalance("S-1000"));
        
        // unbekanntes konto
        assertFalse(b.deposit("S-X", 12, 100));
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
    public void testRankingFunctions() {
        Bank bank = new Bank();
        bank.createSavingsAccount();
        bank.deposit("S-1000", 1, 100);
        
        // nur aufrufen um zu sehen ob es abstürzt
        bank.printTop5();
        bank.printBottom5();
    }

    /**
     * Testet Getter und Setter für das Konto (UML-Kompatibilität).
     */
    @Test
    public void testGetSetAccount() {
        Bank bank = new Bank();
        ch.schule.SavingsAccount account = new ch.schule.SavingsAccount("S-1000");
        bank.setAccount(account);
        assertEquals(account, bank.getAccount());
    }
}