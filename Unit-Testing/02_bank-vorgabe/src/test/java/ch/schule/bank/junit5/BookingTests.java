package test.java.ch.schule.bank.junit5;

import main.java.ch.schule.Booking;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests für einzelne Buchungen.
 */
public class BookingTests {

    @Test
    public void testBookingProperties() {
        Booking booking = new Booking(10, 500);
        assertEquals(10, booking.getDate());
        assertEquals(500, booking.getAmount());

        // Ausgabe testen
        booking.print(1000);
    }
}
