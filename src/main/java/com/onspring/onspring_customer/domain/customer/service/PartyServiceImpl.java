package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
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

    @Override
    public Long saveParty(PartyDto partyDto) {
        return 0L;
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
