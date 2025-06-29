package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.onspring.onspring_customer.domain.user.dto.EndUserPointDto;
import com.onspring.onspring_customer.domain.user.dto.PointDto;
import com.onspring.onspring_customer.domain.user.dto.PointResponseDto;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.entity.Point;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.onspring.onspring_customer.domain.user.repository.PointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock private PartyRepository partyRepository;
    @Mock private EndUserRepository endUserRepository;
    @Mock private PointRepository pointRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private PointServiceImpl pointService;

    private EndUser endUser;
    private Party party;
    private Point point;

    @BeforeEach
    void setUp() {
        endUser = EndUser.builder()
                .id(1L)
                .name("Tester")
                .phone("010-0000-0000")
                .password("pw")
                .isActivated(true)
                .build();

        party = Party.builder()
                .id(99L)
                .name("Test Party")
                .isActivated(true)
                .sunday(true).monday(true).tuesday(true).wednesday(true)
                .thursday(true).friday(true).saturday(true)
                .build();

        point = Point.builder()
                .id(88L)
                .party(party)
                .endUser(endUser)
                .assignedAmount(BigDecimal.valueOf(10000))
                .currentAmount(BigDecimal.valueOf(8000))
                .validThru(LocalDateTime.now().plusDays(30))
                .build();
    }


    @Test
    void testUsePointOnPayment() {
        when(pointRepository.findById(88L)).thenReturn(Optional.of(point));

        boolean result = pointService.usePointOnPayment(88L, BigDecimal.valueOf(1000));

        assertThat(result).isTrue();
        verify(pointRepository).save(point);
        assertThat(point.getCurrentAmount()).isEqualTo(BigDecimal.valueOf(7000));
    }

    @Test
    void testAssignPointToEndUserById_List() {
        when(pointRepository.findByParty_IdAndEndUser_IdIn(99L, List.of(1L))).thenReturn(List.of(point));

        boolean result = pointService.assignPointToEndUserById(List.of(1L), 99L, BigDecimal.valueOf(500), LocalDateTime.now().plusDays(10));

        assertThat(result).isTrue();
        verify(pointRepository).saveAll(any());
    }
}
