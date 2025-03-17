package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Getter
@Service
public class PartyServiceImpl implements PartyService {
    private PartyRepository partyRepository;
    private CustomerRepository customerRepository;

    @Override
    public Long saveParty(PartyDto partyDto) {
        Customer customer = customerRepository.findById(partyDto.getCustomerId())
                .orElseThrow();
        Party party = Party.builder()
                .customer(customer)
                .name(partyDto.getName())
                .period(partyDto.getPeriod())
                .amount(partyDto.getAmount())
                .allowedTimeStart(partyDto.getAllowedTimeStart())
                .allowedTimeEnd(partyDto.getAllowedTimeEnd())
                .validThru(partyDto.getValidThru())
                .sunday(partyDto.isSunday())
                .monday(partyDto.isMonday())
                .tuesday(partyDto.isTuesday())
                .wednesday(partyDto.isWednesday())
                .thursday(partyDto.isThursday())
                .friday(partyDto.isFriday())
                .saturday(partyDto.isSaturday())
                .maximumAmount(partyDto.getMaximumAmount())
                .maximumTransaction(partyDto.getMaximumTransaction())
                .isActivated(partyDto.isActivated())
                .build();

        return partyRepository.save(party)
                .getId();
    }

    @Override
    public PartyDto findPartyById(Long id) {
        return null;
    }

    @Override
    public List<PartyDto> findAllParty() {
        return List.of();
    }

    @Override
    public boolean updateParty(PartyDto partyDto) {
        return false;
    }

    @Override
    public boolean deletePartyById(Long id) {
        return false;
    }

    @Override
    public boolean deleteAllParty(List<Long> ids) {
        return false;
    }
}
