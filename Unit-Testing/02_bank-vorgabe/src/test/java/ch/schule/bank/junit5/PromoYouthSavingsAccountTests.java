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

    /**
     * Testet, dass bei einer Einzahlung 1% Bonus gutgeschrieben wird.
     * 10000 + 1% (100) = 10100
     */
    @Test
    public void testDepositMitBonus() {
        PromoYouthSavingsAccount konto = new PromoYouthSavingsAccount("Y-1000");
        assertTrue(konto.deposit(13576, 10000));
        assertEquals(10100, konto.getBalance());
    }

    /**
     * Testet den Bonus bei einem Betrag, der nicht ganzzahlig durch 100 teilbar ist.
     * 12345 -> Bonus = 12344/100 = 123, eingezahlt 12345 + 123 = 12468
     */
    @Test
    public void testDepositBonusGanzzahlig() {
        PromoYouthSavingsAccount konto = new PromoYouthSavingsAccount("Y-1000");
        konto.deposit(13576, 12345);
        assertEquals(12468, konto.getBalance());
    }

    /**
     * Testet, dass eine negative Einzahlung abgelehnt wird (kein Bonus).
     */
    @Test
    public void testDepositNegativ() {
        PromoYouthSavingsAccount konto = new PromoYouthSavingsAccount("Y-1000");
        assertFalse(konto.deposit(13576, -10000));
        assertEquals(0, konto.getBalance());
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