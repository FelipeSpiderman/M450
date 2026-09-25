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

    @Test
    public void testWithdrawalScenarios() {
        SavingsAccount acc = new SavingsAccount("S-1000");
        acc.deposit(1, 1000);
        
        // normaler bezug
        assertTrue(acc.withdraw(2, 200));
        
        // darf nicht ins minus
        assertFalse(acc.withdraw(3, 900));
        
        // ganzes geld rausnehmen
        assertTrue(acc.withdraw(4, 800));
        assertEquals(0, acc.getBalance());
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