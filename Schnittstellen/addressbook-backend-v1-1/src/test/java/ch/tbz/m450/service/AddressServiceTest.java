package ch.tbz.m450.service;

import ch.tbz.m450.repository.Address;
import ch.tbz.m450.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private Address address1;
    private Address address2;
    private Date now;

    @BeforeEach
    void setUp() {
        now = new Date();
        address1 = new Address(1, "Max", "Muster", "0791234567", now);
        address2 = new Address(2, "Anna", "Adam", "0789876543", now);
    }

    @Test
    void testSaveAddress() {
        when(addressRepository.save(address1)).thenReturn(address1);

        Address saved = addressService.save(address1);

        assertNotNull(saved);
        assertEquals(address1.getId(), saved.getId());
        assertEquals("Max", saved.getFirstname());
        verify(addressRepository, times(1)).save(address1);
    }

    @Test
    void testGetAllAddressesSorted() {
        // Return unsorted list from mocked repository
        when(addressRepository.findAll()).thenReturn(List.of(address1, address2));

        List<Address> result = addressService.getAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        // Adam should be before Muster
        assertEquals("Adam", result.get(0).getLastname());
        assertEquals("Muster", result.get(1).getLastname());
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    void testGetAddressById_Found() {
        when(addressRepository.findById(1)).thenReturn(Optional.of(address1));

        Optional<Address> result = addressService.getAddress(1);

        assertTrue(result.isPresent());
        assertEquals(address1.getId(), result.get().getId());
        assertEquals("Max", result.get().getFirstname());
        verify(addressRepository, times(1)).findById(1);
    }

    @Test
    void testGetAddressById_NotFound() {
        when(addressRepository.findById(99)).thenReturn(Optional.empty());

        Optional<Address> result = addressService.getAddress(99);

        assertFalse(result.isPresent());
        verify(addressRepository, times(1)).findById(99);
    }
}
