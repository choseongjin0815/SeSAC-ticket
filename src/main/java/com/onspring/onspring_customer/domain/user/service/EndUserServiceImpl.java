package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.common.entity.PartyEndUser;
import com.onspring.onspring_customer.domain.common.repository.PartyEndUserRepository;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.entity.QParty;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.dto.PointDto;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.entity.Point;
import com.onspring.onspring_customer.domain.user.entity.QEndUser;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.onspring.onspring_customer.domain.user.repository.PointRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    @Transactional
    public Long saveEndUser(EndUserDto endUserDto) {
        log.info("Saving end user with name {} associated with party IDs {}", endUserDto.getName(),
                endUserDto.getPartyIds());

        // 엔티티가 이미 존재하는지 확인 (업데이트 케이스)
        EndUser endUser;
        boolean isUpdate = endUserDto.getId() != null;

        if (isUpdate) {
            // 기존 사용자 조회
            endUser = getEndUser(endUserDto.getId());
            // 기본 정보 업데이트
            endUser.setName(endUserDto.getName());
            endUser.setPhone(endUserDto.getPhone());
            endUser.setActivated(endUserDto.isActivated());

            // 비밀번호가 제공된 경우에만 업데이트
            if (endUserDto.getPassword() != null && !endUserDto.getPassword().isEmpty()) {
                endUser.setPassword(endUserDto.getPassword());
            }

            // 기존 파티 연결 관계 모두 삭제
            partyEndUserRepository.deleteAll(endUser.getPartyEndUsers());
            endUser.getPartyEndUsers().clear();
        } else {
            // 새 사용자 생성
            endUser = modelMapper.map(endUserDto, EndUser.class);
        }

        // 사용자 저장 또는 업데이트
        endUser = endUserRepository.save(endUser);

        // 모든 파티와의 연결 관계 생성
        if (endUserDto.getPartyIds() != null && !endUserDto.getPartyIds().isEmpty()) {
            for (Long partyId : endUserDto.getPartyIds()) {
                Party party = getParty(partyId);

                PartyEndUser partyEndUser = new PartyEndUser();
                partyEndUser.setParty(party);
                partyEndUser.setEndUser(endUser);

                partyEndUserRepository.save(partyEndUser);
            }
        }

        log.info("Successfully saved/updated end user with ID {}", endUser.getId());

        return endUser.getId();
    }

    @Transactional
    @Override
    public EndUserDto findEndUserById(Long id) {
        EndUser endUser = getEndUser(id);

        List<Long> partyIds = endUser.getPartyEndUsers()
                .stream()
                .map(PartyEndUser::getParty)
                .map(Party::getId)
                .toList();
        List<Long> pointIds = endUser.getPoints()
                .stream()
                .map(Point::getId)
                .toList();

        EndUserDto endUserDto = modelMapper.map(endUser, EndUserDto.class);
        endUserDto.setPartyIds(partyIds);
        endUserDto.setPointIds(pointIds);

        return endUserDto;
    }

    @Override
    public List<EndUserDto> findAllEndUser() {
        List<EndUser> endUserList = endUserRepository.findAll();
        List<EndUserDto> endUserDtoList = new ArrayList<>();

        for (EndUser element : endUserList) {
            EndUserDto map = modelMapper.map(element, EndUserDto.class);
            map.setPartyIds(element.getPartyEndUsers()
                    .stream()
                    .map(PartyEndUser::getParty)
                    .map(Party::getId)
                    .toList());
            map.setPointIds(element.getPoints()
                    .stream()
                    .map(Point::getId)
                    .toList());
            endUserDtoList.add(map);
        }

        return endUserDtoList;
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

        query.where(endUser.isActivated.eq(isActivated));

        Long count = Objects.requireNonNull(query.clone()
                .select(endUser.count())
                .fetchOne());

        query.orderBy(endUser.id.desc());
        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<EndUser> endUserList = query.fetch();

        List<EndUserDto> endUserDtoList = new ArrayList<>();

        for (EndUser element : endUserList) {
            EndUserDto map = modelMapper.map(element, EndUserDto.class);
            map.setPartyIds(element.getPartyEndUsers()
                    .stream()
                    .map(PartyEndUser::getParty)
                    .map(Party::getId)
                    .toList());
            map.setPointIds(element.getPoints()
                    .stream()
                    .map(Point::getId)
                    .toList());
            endUserDtoList.add(map);
        }


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
