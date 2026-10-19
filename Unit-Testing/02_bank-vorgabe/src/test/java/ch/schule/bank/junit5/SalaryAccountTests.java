package test.java.ch.schule.bank.junit5;

import main.java.ch.schule.SalaryAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für das Lohnkonto.
 */
public class SalaryAccountTests {

    @Test
    public void testSalaryAccountWithCreditLimit() {
        // Lohnkonto mit Limite von -5000
        SalaryAccount account = new SalaryAccount("P-1000", -5000);
        assertEquals("P-1000", account.getId());
        assertEquals(0, account.getBalance());

        // Einzahlung
        assertTrue(account.deposit(10, 2000));
        assertEquals(2000, account.getBalance());

        // Überziehen bis zur Kreditlimite ist erlaubt
        assertTrue(account.withdraw(11, 6000));
        assertEquals(-4000, account.getBalance());

        // Mehr als die Kreditlimite überziehen wird abgelehnt
        assertFalse(account.withdraw(12, 2000));
        assertEquals(-4000, account.getBalance());
    }
}