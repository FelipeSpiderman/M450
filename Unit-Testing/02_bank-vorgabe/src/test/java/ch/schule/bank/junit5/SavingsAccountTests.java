package ch.schule.bank.junit5;

import main.java.ch.schule.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für das Sparkonto.
 */
public class SavingsAccountTests {

    @Test
    public void testWithdrawalLimits() {
        SavingsAccount account = new SavingsAccount("S-1000");
        account.deposit(1, 1000);

        // Bezug im Rahmen des Guthabens
        assertTrue(account.withdraw(2, 300));
        assertEquals(700, account.getBalance());

        // Sparkonto darf nicht ins Minus gehen
        assertFalse(account.withdraw(3, 800));
        assertEquals(700, account.getBalance());

        // Restliches Guthaben komplett abheben
        assertTrue(account.withdraw(4, 700));
        assertEquals(0, account.getBalance());
    }
}