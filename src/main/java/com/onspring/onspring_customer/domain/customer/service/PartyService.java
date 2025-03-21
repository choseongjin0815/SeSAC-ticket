package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;

import java.util.List;

public interface PartyService {
    Long saveParty(PartyDto partyDto);

    PartyDto findPartyById(Long id);

    List<PartyDto> findAllParty();

    boolean updateParty(PartyDto partyDto);

    boolean activatePartyById(Long id);

    boolean deactivatePartyById(Long id);
}
