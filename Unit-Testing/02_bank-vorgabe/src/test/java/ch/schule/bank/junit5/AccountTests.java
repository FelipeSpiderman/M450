package test.java.ch.schule.bank.junit5;

import main.java.ch.schule.SavingsAccount;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests für die Account-Basisklasse.
 */
public class AccountTests {

    @Test
    public void testDeposit() {
        SavingsAccount account = new SavingsAccount("S-1000");
        assertEquals(0, account.getBalance());

        // Normale Einzahlung
        assertTrue(account.deposit(100, 500));
        assertEquals(500, account.getBalance());

        // Ungültige Einzahlungen: negativer Betrag oder älteres Datum
        assertFalse(account.deposit(101, -100));
        assertFalse(account.deposit(50, 200));
        assertEquals(500, account.getBalance());
    }

    @Test
    public void testWithdraw() {
        SavingsAccount account = new SavingsAccount("S-1000");
        account.deposit(100, 1000);

        // Erfolgreicher Bezug
        assertTrue(account.withdraw(101, 400));
        assertEquals(600, account.getBalance());

        // Negativer Betrag wird abgelehnt
        assertFalse(account.withdraw(102, -200));
        assertEquals(600, account.getBalance());
    }

    @Test
    public void testTransactionOrder() {
        SavingsAccount account = new SavingsAccount("S-1000");
        account.deposit(100, 100);

        // Transaktionen in der Vergangenheit sind nicht erlaubt
        assertFalse(account.canTransact(90));
        assertTrue(account.canTransact(100));
        assertTrue(account.canTransact(120));
    }

    @Test
    public void testPrint() {
        SavingsAccount account = new SavingsAccount("S-1000");
        account.deposit(100, 1000);
        account.withdraw(101, 200);

        // Kontoauszug drucken
        account.print();
        account.print(1970, 4);
    }
}
