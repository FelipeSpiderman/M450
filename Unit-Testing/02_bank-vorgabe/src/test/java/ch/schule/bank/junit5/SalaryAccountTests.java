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

    /**
     * Testet, dass die Abhebung bis zur Kreditlimite erlaubt ist.
     */
    @Test
    public void testWithdrawBisZurLimite() {
        SalaryAccount konto = new SalaryAccount("P-1000", -5000);
        konto.deposit(13576, 10000);
        assertTrue(konto.withdraw(13577, 15000));
        assertEquals(-5000, konto.getBalance());
    }

    /**
     * Testet, dass die Abhebung über die Kreditlimite hinaus verweigert wird.
     */
    @Test
    public void testWithdrawUeberLimite() {
        SalaryAccount konto = new SalaryAccount("P-1000", -5000);
        konto.deposit(13576, 10000);
        assertFalse(konto.withdraw(13577, 15001));
        assertEquals(10000, konto.getBalance());
    }

    /**
     * Testet erlaubtes Überziehen innerhalb der Limite (Saldo wird negativ).
     */
    @Test
    public void testUeberziehen() {
        SalaryAccount konto = new SalaryAccount("P-1000", -2000);
        konto.deposit(13576, 1000);
        assertTrue(konto.withdraw(13577, 2000));
        assertEquals(-1000, konto.getBalance());
    }
}