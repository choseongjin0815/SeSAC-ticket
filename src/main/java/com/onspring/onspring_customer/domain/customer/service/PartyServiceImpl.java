package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.entity.QParty;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class PartyServiceImpl implements PartyService {
    private final PartyRepository partyRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public PartyServiceImpl(PartyRepository partyRepository, CustomerRepository customerRepository,
                            ModelMapper modelMapper, JPAQueryFactory queryFactory) {
        this.partyRepository = partyRepository;
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.queryFactory = queryFactory;
    }

    private Party getParty(Long id) {
        Optional<Party> result = partyRepository.findById(id);

        return result.orElseThrow(() -> new EntityNotFoundException("Party with ID " + id + " not found"));
    }

    @Override
    public Long saveParty(PartyDto partyDto) {
        log.info("Saving party with name {} associated with customer ID {}", partyDto.getName(),
                partyDto.getCustomerId());

        Customer customer = customerRepository.findById(partyDto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + partyDto.getCustomerId() + " not"
                                                               + " found"));

        Party party = modelMapper.map(partyDto, Party.class);
        party.setCustomer(customer);

        Long id = partyRepository.save(party)
                .getId();

        log.info("Successfully saved party with name {}", partyDto.getName());

        return id;
    }

    @Override
    public PartyDto findPartyById(Long id) {
        Party party = getParty(id);

        PartyDto partyDto = modelMapper.map(party, PartyDto.class);
        partyDto.setCustomerId(party.getCustomer()
                .getId());

        return partyDto;
    }

    @Override
    public List<PartyDto> findAllParty() {
        return partyRepository.findAll()
                .stream()
                .map(element -> modelMapper.map(element, PartyDto.class))
                .toList();
    }

    @Override
    public Page<PartyDto> findAllPartyByQuery(String name, LocalTime allowedTimeStart, LocalTime allowedTimeEnd,
                                              boolean sunday, boolean monday, boolean tuesday, boolean wednesday,
                                              boolean thursday, boolean friday, boolean saturday,
                                              BigDecimal maximumAmount, Long maximumTransaction, Pageable pageable) {
        QParty party = QParty.party;
        JPAQuery<Party> query = queryFactory.selectFrom(party);

        if (name != null) {
            query.where(party.name.containsIgnoreCase(name));
        }
        if (allowedTimeStart != null && allowedTimeEnd != null) {
            query.where(party.allowedTimeStart.before(allowedTimeStart)
                    .and(party.allowedTimeEnd.after(allowedTimeEnd)));
        }
        if (maximumAmount != null) {
            query.where(party.maximumAmount.loe(maximumAmount));
        }
        if (maximumTransaction != null) {
            query.where(party.maximumTransaction.loe(maximumTransaction));
        }
        if (sunday || monday || tuesday || wednesday || thursday || friday || saturday) {
            query.where(party.sunday.eq(sunday));
            query.where(party.monday.eq(monday));
            query.where(party.tuesday.eq(tuesday));
            query.where(party.wednesday.eq(wednesday));
            query.where(party.thursday.eq(thursday));
            query.where(party.friday.eq(friday));
            query.where(party.saturday.eq(saturday));
        }

        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<Party> partyList = query.fetch();

        List<PartyDto> partyDtoList = partyList.stream()
                .map(element -> modelMapper.map(element, PartyDto.class))
                .toList();

        return new PageImpl<>(partyDtoList, pageable, partyDtoList.size());
    }

    @Override
    public boolean updateParty(PartyDto partyDto) {
        log.info("Updating party with ID {}", partyDto.getId());

        Party party = getParty(partyDto.getId());

        modelMapper.map(partyDto, party);
        partyRepository.save(party);

        log.info("Successfully updated party with ID {}", partyDto.getId());

        return true;
    }

    @Override
    public boolean activatePartyById(Long id) {
        log.info("Activating party with ID {}", id);

        Party party = getParty(id);

        party.setActivated(true);
        partyRepository.save(party);

        log.info("Successfully activated party with ID {}", id);

        return true;
    }

    @Override
    public boolean deactivatePartyById(Long id) {
        log.info("Deactivating party with ID {}", id);

        Party party = getParty(id);

        party.setActivated(false);
        partyRepository.save(party);

        log.info("Successfully deactivated party with ID {}", id);

        return true;
    }
}
