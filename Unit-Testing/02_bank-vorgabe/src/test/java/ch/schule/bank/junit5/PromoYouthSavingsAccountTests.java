package ch.schule.bank.junit5;

import ch.schule.PromoYouthSavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für das Promo-Jugend-Sparkonto.
 */
public class PromoYouthSavingsAccountTests {

    @Test
    public void checkBonusCalculation() {
        PromoYouthSavingsAccount account = new PromoYouthSavingsAccount("Y-1000");
        account.deposit(1, 10000);
        
        // 1% Bonus auf 10000 sind 100
        assertEquals(10100, account.getBalance());
        
        // Nochmal einzahlen
        account.deposit(2, 5000);
        // Bonus auf 5000 sind 50 -> 10100 + 5000 + 50 = 15150
        assertEquals(15150, account.getBalance());
    }

    @Test
    public void noNegativeDeposit() {
        PromoYouthSavingsAccount account = new PromoYouthSavingsAccount("Y-1000");
        assertFalse(account.deposit(1, -500));
        assertEquals(0, account.getBalance());
    }

    /**
     * Testet, dass das Konto nicht ins Minus gehen kann (erbt von SavingsAccount).
     */
    @Test
    public void testWithdrawNichtGenugSaldo() {
        PromoYouthSavingsAccount konto = new PromoYouthSavingsAccount("Y-1000");
        konto.deposit(13576, 10000);
        assertFalse(konto.withdraw(13577, 10101));
        assertEquals(10100, konto.getBalance());
    }
}