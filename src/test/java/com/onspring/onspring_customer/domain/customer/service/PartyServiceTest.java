package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartyServiceTest {
    @Mock
    private PartyRepository partyRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PartyServiceImpl partyService;

    private PartyDto partyDto;
    private Party party;
    private Customer customer;

    @BeforeEach
    void setUp() {
        partyDto = new PartyDto();
        partyDto.setId(1L);
        partyDto.setCustomerId(1L);
        partyDto.setActivated(true);

        customer = new Customer();

        party = Party.builder()
                .id(1L)
                .name("party")
                .customer(customer)
                .isActivated(true)
                .build();
    }

    @Test
    void testSaveParty() {
        when(modelMapper.map(any(PartyDto.class), any())).thenReturn(party);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(partyRepository.save(any(Party.class))).thenReturn(party);

        Long partyId = partyService.saveParty(partyDto);

        verify(partyRepository).save(party);

        assertEquals(party.getId(), partyId);
    }

    @Test
    void testFindPartyById() {
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
        when(modelMapper.map(any(Party.class), any())).thenReturn(partyDto);

        PartyDto partyDto1 = partyService.findPartyById(1L);

        assertNotNull(partyDto1);
        assertEquals(partyDto.getId(), partyDto1.getId());
    }

    @Test
    void testFindPartyById_NotFound() {
        when(partyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> partyService.findPartyById(1L));
    }

    @Test
    void testUpdateParty() {
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));

        boolean result = partyService.updateParty(partyDto);

        assertTrue(result);

        verify(partyRepository).save(party);
    }

    @Test
    void testActivatePartyById() {
        party.setActivated(false);

        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));

        boolean result = partyService.activatePartyById(1L);

        assertTrue(result);

        verify(partyRepository).save(party);

        assertTrue(party.isActivated());
    }

    @Test
    void testDeactivatePartyById() {
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));

        boolean result = partyService.deactivatePartyById(1L);

        assertTrue(result);

        verify(partyRepository).save(party);

        assertFalse(party.isActivated());
    }
}
