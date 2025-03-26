package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.entity.QParty;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartyServiceTest {
    @Mock
    private PartyRepository partyRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private JPAQueryFactory queryFactory;

    @Mock
    private JPAQuery<Party> query;

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
    void testFindAllPartyByQuery() {
        party.setAllowedTimeStart(LocalTime.of(9, 0));
        party.setAllowedTimeEnd(LocalTime.of(18, 0));
        String name = "party";
        LocalTime allowedTimeStart = LocalTime.of(9, 0);
        LocalTime allowedTimeEnd = LocalTime.of(18, 0);
        boolean sunday = true;
        boolean monday = false;
        boolean tuesday = false;
        boolean wednesday = false;
        boolean thursday = false;
        boolean friday = false;
        boolean saturday = false;
        BigDecimal maximumAmount = new BigDecimal("10000");
        Long maximumTransaction = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        List<Party> parties = Collections.singletonList(party);

        when(queryFactory.selectFrom(QParty.party)).thenReturn(query);
        when(query.where(any(Predicate.class))).thenReturn(query);
        when(query.offset(anyLong())).thenReturn(query);
        when(query.limit(anyLong())).thenReturn(query);
        when(query.fetch()).thenReturn(parties);

        when(modelMapper.map(party, PartyDto.class)).thenReturn(partyDto);

        Page<PartyDto> result = partyService.findAllPartyByQuery(name, allowedTimeStart, allowedTimeEnd, sunday,
                monday, tuesday, wednesday, thursday, friday, saturday, maximumAmount, maximumTransaction, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(partyDto, result.getContent()
                .get(0));
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
