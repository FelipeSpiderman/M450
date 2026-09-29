package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AddressComparatorTest {

    private AddressComparator comparator;
    private Date now;

    @BeforeEach
    void setUp() {
        comparator = new AddressComparator();
        now = new Date();
    }

    @Test
    void testCompareDifferentLastname() {
        Address a1 = new Address(1, "Hans", "Adam", "0791112233", now);
        Address a2 = new Address(2, "Beat", "Brunner", "0792223344", now);

        assertTrue(comparator.compare(a1, a2) < 0);
        assertTrue(comparator.compare(a2, a1) > 0);
    }

    @Test
    void testCompareSameLastnameDifferentFirstname() {
        Address a1 = new Address(1, "Beat", "Muster", "0791112233", now);
        Address a2 = new Address(2, "Hans", "Muster", "0792223344", now);

        assertTrue(comparator.compare(a1, a2) < 0);
        assertTrue(comparator.compare(a2, a1) > 0);
    }

    @Test
    void testCompareSameNameDifferentPhonenumber() {
        Address a1 = new Address(1, "Hans", "Muster", "0791111111", now);
        Address a2 = new Address(2, "Hans", "Muster", "0799999999", now);

        assertTrue(comparator.compare(a1, a2) < 0);
        assertTrue(comparator.compare(a2, a1) > 0);
    }

    @Test
    void testCompareSameNameAndPhoneDifferentDate() {
        Date earlier = new Date(1000000L);
        Date later = new Date(2000000L);

        Address a1 = new Address(1, "Hans", "Muster", "0791112233", earlier);
        Address a2 = new Address(2, "Hans", "Muster", "0791112233", later);

        assertTrue(comparator.compare(a1, a2) < 0);
        assertTrue(comparator.compare(a2, a1) > 0);
    }

    @Test
    void testCompareSameNamePhoneAndDateDifferentId() {
        Address a1 = new Address(1, "Hans", "Muster", "0791112233", now);
        Address a2 = new Address(2, "Hans", "Muster", "0791112233", now);

        assertTrue(comparator.compare(a1, a2) < 0);
        assertTrue(comparator.compare(a2, a1) > 0);
    }

    @Test
    void testCompareIdenticalAddresses() {
        Address a1 = new Address(1, "Hans", "Muster", "0791112233", now);
        Address a2 = new Address(1, "Hans", "Muster", "0791112233", now);

        assertEquals(0, comparator.compare(a1, a2));
    }

    @Test
    void testCompareWithNullAddresses() {
        Address a1 = new Address(1, "Hans", "Muster", "0791112233", now);

        assertEquals(0, comparator.compare(null, null));
        assertTrue(comparator.compare(null, a1) > 0);
        assertTrue(comparator.compare(a1, null) < 0);
    }

    @Test
    void testCompareWithNullAttributes() {
        Address a1 = new Address(1, null, "Muster", null, null);
        Address a2 = new Address(2, "Hans", "Muster", "0791112233", now);

        assertTrue(comparator.compare(a1, a2) > 0);
        assertTrue(comparator.compare(a2, a1) < 0);
    }

    @Test
    void testSortingListWithComparator() {
        Address a1 = new Address(1, "Zoe", "Zimmermann", "0793", now);
        Address a2 = new Address(2, "Anna", "Adam", "0791", now);
        Address a3 = new Address(3, "Ben", "Adam", "0792", now);

        List<Address> list = new ArrayList<>(List.of(a1, a2, a3));
        list.sort(comparator);

        assertEquals("Adam", list.get(0).getLastname());
        assertEquals("Anna", list.get(0).getFirstname());
        assertEquals("Adam", list.get(1).getLastname());
        assertEquals("Ben", list.get(1).getFirstname());
        assertEquals("Zimmermann", list.get(2).getLastname());
    }
}
