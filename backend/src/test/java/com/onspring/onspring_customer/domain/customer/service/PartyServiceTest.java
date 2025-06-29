package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.entity.Point;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.onspring.onspring_customer.domain.user.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyServiceTest {

    @Mock
    private PointRepository pointRepository;
    @Mock
    private EndUserRepository endUserRepository;
    @Mock
    private PartyRepository partyRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PartyServiceImpl partyService;

    private Customer customer;
    private Party party;
    private PartyDto partyDto;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .name("TestCustomer")
                .address("Seoul")
                .phone("01012345678")
                .isActivated(true)
                .build();

        party = Party.builder()
                .id(1L)
                .name("TestParty")
                .customer(customer)
                .isActivated(true)
                .build();

        partyDto = PartyDto.builder()
                .id(1L)
                .name("TestParty")
                .customerId(1L)
                .isActivated(true)
                .build();
    }

    @Test
    void testSaveParty() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(modelMapper.map(partyDto, Party.class)).thenReturn(party);
        when(partyRepository.save(any())).thenReturn(party);

        Long result = partyService.saveParty(partyDto);
        assertEquals(1L, result);
    }


    @Test
    void testUpdateParty() {
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));

        partyDto.setName("UpdatedParty");
        boolean result = partyService.updateParty(partyDto);

        assertTrue(result);
        verify(partyRepository).save(any());
    }

    @Test
    void testActivatePartyById() {
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));

        boolean result = partyService.activatePartyById(List.of(1L));
        assertTrue(result);
        verify(partyRepository).save(party);
    }

    @Test
    void testDeactivatePartyById() {
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));

        boolean result = partyService.deactivatePartyById(List.of(1L));
        assertTrue(result);
        verify(partyRepository).save(party);
    }

    @Test
    void testAddEndUserToParty() {
        EndUser user = EndUser.builder().id(100L).build();
        when(partyRepository.findById(1L)).thenReturn(Optional.of(party));
        when(endUserRepository.findAllById(List.of(100L))).thenReturn(List.of(user));

        boolean[] called = {false};
        when(pointRepository.saveAll(any())).thenAnswer(invocation -> {
            called[0] = true;
            return invocation.getArgument(0);
        });

        List<Long> ids = partyService.addEndUserToParty(1L, List.of(100L));
        assertTrue(called[0]);
        assertEquals(1, ids.size());
    }
}