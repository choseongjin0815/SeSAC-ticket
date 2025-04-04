package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.common.entity.PartyEndUser;
import com.onspring.onspring_customer.domain.common.repository.PartyEndUserRepository;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.entity.QParty;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.dto.PointDto;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.entity.QEndUser;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.onspring.onspring_customer.domain.user.repository.PointRepository;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
public class EndUserServiceImpl implements EndUserService {
    private final EndUserRepository endUserRepository;
    private final PartyRepository partyRepository;
    private final PartyEndUserRepository partyEndUserRepository;
    private final PointRepository pointRepository;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public EndUserServiceImpl(EndUserRepository endUserRepository, PartyRepository partyRepository,
                              PartyEndUserRepository partyEndUserRepository, PointRepository pointRepository,
                              ModelMapper modelMapper, JPAQueryFactory queryFactory) {
        this.endUserRepository = endUserRepository;
        this.partyRepository = partyRepository;
        this.partyEndUserRepository = partyEndUserRepository;
        this.pointRepository = pointRepository;
        this.modelMapper = modelMapper;
        this.queryFactory = queryFactory;
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
    public Page<EndUserDto> findAllEndUserByQuery(String name, String partyName, String phone, boolean isActivated,
                                                  Pageable pageable) {
        QEndUser endUser = QEndUser.endUser;
        JPAQuery<EndUser> query = queryFactory.selectFrom(endUser);

        if (name != null) {
            query.where(endUser.name.containsIgnoreCase(name));
        }
        if (partyName != null) {
            QParty party = QParty.party;
            List<Long> partyIds = queryFactory.select(party.id)
                    .from(party)
                    .where(party.name.containsIgnoreCase(partyName))
                    .fetch();

            query.where(endUser.partyEndUsers.any().party.id.in(partyIds));
        }
        if (phone != null) {
            query.where(endUser.phone.contains(phone));
        }
        if (isActivated) {
            query.where(endUser.isActivated);
        }

        Long count = Objects.requireNonNull(query.clone()
                .select(endUser.count())
                .fetchOne());

        query.orderBy(endUser.id.desc());
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<EndUser> endUserList = query.fetch();

        List<EndUserDto> endUserDtoList = endUserList.stream()
                .map(element -> modelMapper.map(element, EndUserDto.class))
                .toList();

        return new PageImpl<>(endUserDtoList, pageable, count);
    }

    @Override
    public Page<PointDto> findPointByEndUserId(Long id, Pageable pageable) {
        return pointRepository.findByEndUser_Id(id, pageable)
                .map(element -> modelMapper.map(element, PointDto.class));
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
    public List<Boolean> activateEndUserById(List<Long> ids) {
        return ids.stream()
                .map(this::activateEndUserById)
                .toList();
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

    @Override
    public List<Boolean> deactivateEndUserById(List<Long> ids) {
        return ids.stream()
                .map(this::deactivateEndUserById)
                .toList();
    }
}
