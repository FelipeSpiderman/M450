package ch.schule.bank.junit5;

import ch.schule.Booking;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests für die Klasse Booking.
 */
public class BookingTests {

    /**
     * Testet die Erzeugung einer Buchung (Datum und Betrag).
     */
    @Test
    public void testInitialization() {
        Booking b = new Booking(13576, 12000);
        assertEquals(13576, b.getDate());
        assertEquals(12000, b.getAmount());
    }

    /**
     * Testet die Erzeugung einer Buchung mit negativem Betrag
     * (z. B. bei einer Abhebung).
     */
    @Test
    public void testInitializationNegativ() {
        Booking b = new Booking(13576, -5000);
        assertEquals(-5000, b.getAmount());
    }

    /**
     * Testet, dass print() keine Exception wirft.
     */
    @Test
    public void testPrint() {
        Booking b = new Booking(13576, 12000);
        b.print(0);
    }
}
