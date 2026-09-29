package ch.tbz.m450.util;

import ch.tbz.m450.repository.Address;

import java.util.Comparator;

public class AddressComparator implements Comparator<Address> {

    @Override
    public int compare(Address a1, Address a2) {
        if (a1 == null && a2 == null) {
            return 0;
        }
        if (a1 == null) {
            return 1;
        }
        if (a2 == null) {
            return -1;
        }

        int result = compareNullable(a1.getLastname(), a2.getLastname());
        if (result != 0) {
            return result;
        }

        result = compareNullable(a1.getFirstname(), a2.getFirstname());
        if (result != 0) {
            return result;
        }

        result = compareNullable(a1.getPhonenumber(), a2.getPhonenumber());
        if (result != 0) {
            return result;
        }

        if (a1.getRegistrationDate() != null || a2.getRegistrationDate() != null) {
            if (a1.getRegistrationDate() == null) {
                return 1;
            }
            if (a2.getRegistrationDate() == null) {
                return -1;
            }
            result = a1.getRegistrationDate().compareTo(a2.getRegistrationDate());
            if (result != 0) {
                return result;
            }
        }

        return Integer.compare(a1.getId(), a2.getId());
    }

    private int compareNullable(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return 0;
        }
        if (s1 == null) {
            return 1;
        }
        if (s2 == null) {
            return -1;
        }
        return s1.compareToIgnoreCase(s2);
    }
}
