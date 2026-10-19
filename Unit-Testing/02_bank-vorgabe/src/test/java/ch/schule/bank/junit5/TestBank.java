package test.java.ch.schule.bank.junit5;

import main.java.ch.schule.BankUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestBank {

    @Test
    public void testBankUtils() {
        assertNotNull(BankUtils.formatBankDate(13576));
        assertNotNull(BankUtils.formatAmount(12000));
    }
}
