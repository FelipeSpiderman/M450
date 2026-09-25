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
    public void checkInitialValues() {
        SavingsAccount account = new SavingsAccount("S-1000");
        assertEquals("S-1000", account.getId());
        assertEquals(0, account.getBalance());
    }

    /**
     * Testet das Einzahlen auf ein Konto.
     */
    @Test
    public void depositShouldUpdateBalance() {
        SavingsAccount account = new SavingsAccount("S-1000");
        account.deposit(100, 500);
        assertEquals(500, account.getBalance());
        
        // negative Beträge sollten ignoriert werden
        account.deposit(101, -100);
        assertEquals(500, account.getBalance());
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
    public void withdrawMoney() {
        SavingsAccount account = new SavingsAccount("S-1000");
        account.deposit(100, 1000);
        boolean success = account.withdraw(101, 400);
        
        assertTrue(success);
        assertEquals(600, account.getBalance());
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
    public void transactionsMustBeInOrder() {
        SavingsAccount account = new SavingsAccount("S-1000");
        account.deposit(100, 100);
        
        // darf nicht in der Vergangenheit liegen
        assertFalse(account.canTransact(50));
        assertTrue(account.canTransact(150));
    }

    /**
     * Testet, dass print() keine Exception wirft.
     */
    @Test
    public void testPrint() {
        SavingsAccount account = new SavingsAccount("S-1000");
        account.deposit(100, 200);
        account.print();
    }

    @Test
    public void testMonthlyPrint() {
        SavingsAccount account = new SavingsAccount("S-1000");
        account.deposit(100, 1000);
        // einfach nur schauen ob es läuft
        account.print(2023, 5);
    }

    /**
     * Testet Getter und Setter für die Buchung (UML-Kompatibilität).
     */
    @Test
    public void testGetSetBooking() {
        SavingsAccount konto = new SavingsAccount("S-1000");
        ch.schule.Booking booking = new ch.schule.Booking(13576, 1000);
        konto.setBooking(booking);
        assertEquals(booking, konto.getBooking());
    }
}
