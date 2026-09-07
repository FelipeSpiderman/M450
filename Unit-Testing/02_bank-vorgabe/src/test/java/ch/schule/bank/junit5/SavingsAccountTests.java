package ch.schule.bank.junit5;

import ch.schule.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für die Klasse SavingsAccount.
 */
public class SavingsAccountTests {

    /**
     * Testet, dass ein Sparkonto nicht ins Minus gehen darf.
     */
    @Test
    public void testWithdrawNichtGenugSaldo() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        konto.deposit(13576, 12000);
        assertFalse(konto.withdraw(13576, 12001));
        assertEquals(12000, konto.getBalance());
    }

    /**
     * Testet, dass eine Abhebung bis zum gesamten Saldo erlaubt ist.
     */
    @Test
    public void testWithdrawGesamtesSaldo() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        konto.deposit(13576, 12000);
        assertTrue(konto.withdraw(13576, 12000));
        assertEquals(0, konto.getBalance());
    }

    /**
     * Testet, dass eine Abhebung mit genügend Saldo funktioniert.
     */
    @Test
    public void testWithdraw() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        konto.deposit(13576, 12000);
        assertTrue(konto.withdraw(13576, 2000));
        assertEquals(10000, konto.getBalance());
    }

    /**
     * Testet den Konstruktor mit leerer ID.
     */
    @Test
    public void testEmptyId() {
        SavingsAccount konto = new SavingsAccount("");
        assertEquals("", konto.getId());
        assertEquals(0, konto.getBalance());
    }
}