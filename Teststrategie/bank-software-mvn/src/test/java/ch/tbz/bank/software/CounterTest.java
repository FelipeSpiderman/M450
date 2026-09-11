package ch.tbz.bank.software;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 * Unit-Tests für die Klasse {@link Counter}.
 *
 * <p>Deckung der Testbasis M450 (Abschnitt "Unit-Tests"):
 *   - init
 *   - increment
 *   - reset
 *   - getCurrentCount
 */
class CounterTest {

    @Test
    void init_setztZaehlerAufNull() {
        Counter counter = new Counter();
        counter.init();
        assertEquals(0, counter.getCurrentCount());
    }

    @Test
    void increment_erhöhtZaehler_umsEine() {
        Counter counter = new Counter();
        counter.init();
        counter.increment();
        assertEquals(1, counter.getCurrentCount());
    }

    @Test
    void increment_beiMehrfachenAufrufen_kumuliert() {
        Counter counter = new Counter();
        counter.init();
        counter.increment();
        counter.increment();
        counter.increment();
        assertEquals(3, counter.getCurrentCount());
    }

    @Test
    void reset_setztZaehlerZurückAufNull() {
        Counter counter = new Counter();
        counter.init();
        counter.increment();
        counter.increment();
        assertEquals(2, counter.getCurrentCount());
        counter.reset();
        assertEquals(0, counter.getCurrentCount());
    }

    @Test
    void init_resettertZaehler_auchNachInkrement() {
        Counter counter = new Counter();
        counter.init();
        counter.increment();
        counter.increment();
        counter.init();
        assertEquals(0, counter.getCurrentCount());
    }
}
