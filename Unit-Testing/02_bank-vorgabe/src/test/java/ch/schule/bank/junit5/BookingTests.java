package ch.schule.bank.junit5;

import ch.schule.Booking;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests für die Klasse Booking.
 */
public class BookingTests {

    @Test
    public void testBookingProperties() {
        Booking b1 = new Booking(10, 500);
        assertEquals(10, b1.getDate());
        assertEquals(500, b1.getAmount());
        
        Booking b2 = new Booking(20, -100);
        assertEquals(-100, b2.getAmount());
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
