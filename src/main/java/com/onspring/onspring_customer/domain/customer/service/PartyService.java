package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.common.dto.PartyEndUserDto;
import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

public interface PartyService {
    Long saveParty(PartyDto partyDto);

    PartyDto findPartyById(Long id);

    PartyDto findPartyByUserId(Long userId);

    List<PartyDto> findAllParty();

    Page<PartyDto> findAllPartyByQuery(String name, LocalTime allowedTimeStart, LocalTime allowedTimeEnd,
                                       boolean sunday, boolean monday, boolean tuesday, boolean wednesday,
                                       boolean thursday, boolean friday, boolean saturday,
                                       BigDecimal maximumAmount, Long maximumTransaction, Pageable pageable);

    Page<PartyEndUserDto> findAllPartyEndUserByQuery(String name, Pageable pageable);

    boolean updateParty(PartyDto partyDto);

    boolean activatePartyById(Long id);

    boolean deactivatePartyById(Long id);
}
