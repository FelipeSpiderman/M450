package ch.tbz.m450.controller;

import ch.tbz.m450.repository.Address;
import ch.tbz.m450.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock
    private AddressService addressService;

    @InjectMocks
    private AddressController addressController;

    private Address sampleAddress;

    @BeforeEach
    void setUp() {
        sampleAddress = new Address(1, "Max", "Muster", "0791234567", new Date());
    }

    @Test
    void testCreateAddress() {
        when(addressService.save(sampleAddress)).thenReturn(sampleAddress);

        ResponseEntity<Address> response = addressController.createAddress(sampleAddress);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Max", response.getBody().getFirstname());
        verify(addressService, times(1)).save(sampleAddress);
    }

    @Test
    void testGetAddresses() {
        when(addressService.getAll()).thenReturn(List.of(sampleAddress));

        ResponseEntity<List<Address>> response = addressController.getAddresses();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(addressService, times(1)).getAll();
    }

    @Test
    void testGetAddressById_Found() {
        when(addressService.getAddress(1)).thenReturn(Optional.of(sampleAddress));

        ResponseEntity<Address> response = addressController.getAddress(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        verify(addressService, times(1)).getAddress(1);
    }

    @Test
    void testGetAddressById_NotFound() {
        when(addressService.getAddress(99)).thenReturn(Optional.empty());

        ResponseEntity<Address> response = addressController.getAddress(99);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(addressService, times(1)).getAddress(99);
    }
}
