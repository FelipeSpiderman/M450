package ch.schule.bank.junit5;

import ch.schule.Account;
import ch.schule.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für die Klasse Account.
 * Da Account abstrakt ist, verwenden wir SavingsAccount als konkretes Konto.
 */
public class AccountTests {

    /**
     * Testet die Initialisierung eines Kontos.
     */
    @Test
    public void testInit() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        assertEquals("S-1000", konto.getId());
        assertEquals(0, konto.getBalance());
    }

    /**
     * Testet das Einzahlen auf ein Konto.
     */
    @Test
    public void testDeposit() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        assertTrue(konto.deposit(13576, 12000));
        assertEquals(12000, konto.getBalance());
    }

    /**
     * Testet, dass eine Einzahlung mit negativem Betrag abgelehnt wird.
     */
    @Test
    public void testDepositNegativ() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        assertFalse(konto.deposit(13576, -12000));
        assertEquals(0, konto.getBalance());
    }

    /**
     * Testet, dass eine Einzahlung mit altem Datum abgelehnt wird.
     */
    @Test
    public void testDepositZuAlt() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        assertTrue(konto.deposit(13576, 12000));
        assertFalse(konto.deposit(13575, 1000));
        assertEquals(12000, konto.getBalance());
    }

    /**
     * Testet das Abheben vom Konto (bei genügend Saldo).
     */
    @Test
    public void testWithdraw() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        konto.deposit(13576, 12000);
        assertTrue(konto.withdraw(13576, 5000));
        assertEquals(7000, konto.getBalance());
    }

    /**
     * Testet, dass eine Abhebung mit negativem Betrag abgelehnt wird.
     */
    @Test
    public void testWithdrawNegativ() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        konto.deposit(13576, 12000);
        assertFalse(konto.withdraw(13576, -5000));
        assertEquals(12000, konto.getBalance());
    }

    /**
     * Testet, dass die Referenzierung über die Basisklasse funktioniert.
     */
    @Test
    public void testReferences() {
        Account konto = new SavingsAccount("S-1000");
        konto.deposit(13576, 12000);
        assertEquals(12000, konto.getBalance());
        assertTrue(konto instanceof SavingsAccount);
    }

    /**
     * Testet das canTransact-Flag: ein leeres Konto akzeptiert jedes Datum,
     * danach nur noch Buchungen mit neuem (nicht älterem) Datum.
     */
    @Test
    public void testCanTransact() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        assertTrue(konto.canTransact(13576));
        konto.deposit(13576, 12000);
        assertTrue(konto.canTransact(13576));
        assertFalse(konto.canTransact(13575));
    }

    /**
     * Testet, dass print() keine Exception wirft.
     */
    @Test
    public void testPrint() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        konto.deposit(13576, 12000);
        konto.withdraw(13577, 5000);
        konto.print();
    }

    /**
     * Testet, dass print(year, month) keine Exception wirft.
     */
    @Test
    public void testMonthlyPrint() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        konto.deposit(13576, 12000);
        konto.withdraw(13577, 5000);
        konto.print(2008, 1);
    }
}
