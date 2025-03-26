package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.common.entity.PartyEndUser;
import com.onspring.onspring_customer.domain.common.repository.PartyEndUserRepository;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class EndUserServiceImpl implements EndUserService {
    private final EndUserRepository endUserRepository;
    private final PartyRepository partyRepository;
    private final PartyEndUserRepository partyEndUserRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public EndUserServiceImpl(EndUserRepository endUserRepository, PartyRepository partyRepository,
                              PartyEndUserRepository partyEndUserRepository, ModelMapper modelMapper) {
        this.endUserRepository = endUserRepository;
        this.partyRepository = partyRepository;
        this.partyEndUserRepository = partyEndUserRepository;
        this.modelMapper = modelMapper;
    }

    private EndUser getEndUser(Long id) {
        Optional<EndUser> result = endUserRepository.findById(id);

        return result.orElseThrow(() -> new EntityNotFoundException("EndUser with ID " + id + " not found"));
    }

    private Party getParty(Long id) {
        return partyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Party with ID " + id + " not found"));
    }

    @Override
    public Long saveEndUser(EndUserDto endUserDto) {
        log.info("Saving end user with name {} associated with party ID {}", endUserDto.getName(),
                endUserDto.getPartyId());

        Party party = getParty(endUserDto.getPartyId());

        EndUser endUser = modelMapper.map(endUserDto, EndUser.class);

        Long id = endUserRepository.save(endUser)
                .getId();

        PartyEndUser partyEndUser = new PartyEndUser();
        partyEndUser.setParty(party);
        partyEndUser.setEndUser(endUser);

        partyEndUserRepository.save(partyEndUser);

        log.info("Successfully saved end user with name {} associated with party ID {}", endUserDto.getName(),
                endUserDto.getPartyId());

        return id;
    }

    @Override
    public EndUserDto findEndUserById(Long id) {
        EndUser endUser = getEndUser(id);

        return modelMapper.map(endUser, EndUserDto.class);
    }

    @Override
    public List<EndUserDto> findAllEndUser() {
        return endUserRepository.findAll()
                .stream()
                .map(element -> modelMapper.map(element, EndUserDto.class))
                .toList();
    }

    @Override
    public boolean updateEndUserPasswordById(Long id, String password) {
        log.info("Updating password for end user with ID {}", id);

        EndUser endUser = getEndUser(id);
        endUser.setPassword(password);

        endUserRepository.save(endUser);

        log.info("Successfully updated password for end user with ID {}", id);

        return true;
    }

    @Override
    public boolean activateEndUserById(Long id) {
        log.info("Activating end user with ID {}", id);

        EndUser endUser = getEndUser(id);
        endUser.setActivated(true);

        endUserRepository.save(endUser);

        log.info("Successfully activated end user with ID {}", id);

        return true;
    }

    @Override
    public boolean deactivateEndUserById(Long id) {
        log.info("Deactivating end user with ID {}", id);

        EndUser endUser = getEndUser(id);
        endUser.setActivated(false);

        endUserRepository.save(endUser);

        log.info("Successfully deactivated end user with ID {}", id);

        return true;
    }
}
