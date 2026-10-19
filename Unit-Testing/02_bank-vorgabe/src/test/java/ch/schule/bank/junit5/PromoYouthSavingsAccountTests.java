package test.java.ch.schule.bank.junit5;

import main.java.ch.schule.PromoYouthSavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für das Jugend-Sparkonto mit Promo-Bonus.
 */
public class PromoYouthSavingsAccountTests {

    @Test
    public void testDepositWithBonus() {
        PromoYouthSavingsAccount account = new PromoYouthSavingsAccount("Y-1000");

        // 1% Bonus auf 10'000 ergibt 100 extra
        assertTrue(account.deposit(1, 10000));
        assertEquals(10100, account.getBalance());

        // Negative Einzahlung gibt keinen Bonus und wird abgelehnt
        assertFalse(account.deposit(2, -500));
        assertEquals(10100, account.getBalance());
    }

    @Test
    public void testWithdrawCannotOverdraw() {
        PromoYouthSavingsAccount account = new PromoYouthSavingsAccount("Y-1000");
        account.deposit(1, 10000);

        // Abheben bis zum Saldo erlaubt
        assertTrue(account.withdraw(2, 5000));
        assertEquals(5100, account.getBalance());

        // Überziehen nicht erlaubt
        assertFalse(account.withdraw(3, 6000));
        assertEquals(5100, account.getBalance());
    }
}