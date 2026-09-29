package ch.tbz.m450.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    private Address address;
    private Date testDate;

    @BeforeEach
    void setUp() {
        testDate = new Date();
        address = new Address(1, "Max", "Muster", "0791234567", testDate);
    }

    @Test
    void testCreateAddressWithAllArgsConstructor() {
        assertNotNull(address);
        assertEquals(1, address.getId());
        assertEquals("Max", address.getFirstname());
        assertEquals("Muster", address.getLastname());
        assertEquals("0791234567", address.getPhonenumber());
        assertEquals(testDate, address.getRegistrationDate());
    }

    @Test
    void testCreateAddressWithNoArgsConstructorAndSetters() {
        Address emptyAddress = new Address();
        Date newDate = new Date();

        emptyAddress.setId(2);
        emptyAddress.setFirstname("Anna");
        emptyAddress.setLastname("Schmidt");
        emptyAddress.setPhonenumber("0789876543");
        emptyAddress.setRegistrationDate(newDate);

        assertEquals(2, emptyAddress.getId());
        assertEquals("Anna", emptyAddress.getFirstname());
        assertEquals("Schmidt", emptyAddress.getLastname());
        assertEquals("0789876543", emptyAddress.getPhonenumber());
        assertEquals(newDate, emptyAddress.getRegistrationDate());
    }
}
