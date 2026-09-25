package ch.schule.bank.junit5;

import ch.schule.SalaryAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für die Klasse SalaryAccount.
 */
public class SalaryAccountTests {

    /**
     * Testet die Initialisierung mit Kreditlimite.
     */
    @Test
    public void testInit() {
        SalaryAccount konto = new SalaryAccount("P-1000", -5000);
        assertEquals("P-1000", konto.getId());
        assertEquals(0, konto.getBalance());
    }

    /**
     * Testet die Einzahlung auf ein Lohnkonto.
     */
    @Test
    public void testDeposit() {
        SalaryAccount konto = new SalaryAccount("P-1000", -5000);
        assertTrue(konto.deposit(13576, 12000));
        assertEquals(12000, konto.getBalance());
    }

    @Test
    public void testWithdrawLimits() {
        SalaryAccount account = new SalaryAccount("P-1000", -5000);
        account.deposit(100, 1000);
        
        // abheben im rahmen der limite
        assertTrue(account.withdraw(101, 4000));
        assertEquals(-3000, account.getBalance());
        
        // zu viel abheben
        assertFalse(account.withdraw(102, 3000)); 
    }
}