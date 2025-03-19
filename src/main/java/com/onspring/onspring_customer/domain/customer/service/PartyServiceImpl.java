package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class PartyServiceImpl implements PartyService {
    private final PartyRepository partyRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public PartyServiceImpl(PartyRepository partyRepository, CustomerRepository customerRepository,
                            ModelMapper modelMapper) {
        this.partyRepository = partyRepository;
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
    }

    private Party getParty(Long id) {
        Optional<Party> result = partyRepository.findById(id);

        return result.orElseThrow(() -> new EntityNotFoundException("Party with ID " + id + " not found"));
    }

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
        Party party = getParty(id);

        return PartyDto.builder()
                .id(party.getId())
                .customerId(party.getCustomer()
                        .getId())
                .name(party.getName())
                .period(party.getPeriod())
                .amount(party.getAmount())
                .allowedTimeStart(party.getAllowedTimeStart())
                .allowedTimeEnd(party.getAllowedTimeEnd())
                .validThru(party.getValidThru())
                .sunday(party.isSunday())
                .monday(party.isMonday())
                .tuesday(party.isTuesday())
                .wednesday(party.isWednesday())
                .thursday(party.isThursday())
                .friday(party.isFriday())
                .saturday(party.isSaturday())
                .maximumAmount(party.getMaximumAmount())
                .maximumTransaction(party.getMaximumTransaction())
                .isActivated(party.isActivated())
                .build();
    }

    @Override
    public List<PartyDto> findAllParty() {
        return List.of();
    }

    @Override
    public boolean updateParty(PartyDto partyDto) {

        party.setName(partyDto.getName());
        party.setPeriod(partyDto.getPeriod());
        party.setAmount(partyDto.getAmount());
        party.setAllowedTimeStart(partyDto.getAllowedTimeStart());
        party.setAllowedTimeEnd(party.getAllowedTimeEnd());
        party.setValidThru(partyDto.getValidThru());
        party.setSunday(partyDto.isSunday());
        party.setMonday(partyDto.isMonday());
        party.setTuesday(partyDto.isTuesday());
        party.setWednesday(partyDto.isWednesday());
        party.setThursday(partyDto.isThursday());
        party.setFriday(partyDto.isFriday());
        party.setSaturday(partyDto.isSaturday());
        party.setMaximumAmount(partyDto.getMaximumAmount());
        party.setMaximumTransaction(partyDto.getMaximumTransaction());
        Party party = getParty(partyDto.getId());
        partyRepository.save(party);

        return true;
    }

    @Override
        return false;
    public boolean activatePartyById(Long id) {
    }

    @Override
        return false;
    public boolean deactivatePartyById(Long id) {
    }
}
