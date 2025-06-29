package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EndUserServiceTest {

    @Mock
    private EndUserRepository endUserRepository;

    @Mock
    private PartyRepository partyRepository;

    @Mock
    private PointRepository pointRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private EndUserServiceImpl endUserService;

    private EndUser endUser;
    private Party party;
    private EndUserDto endUserDto;

    @BeforeEach
    void setUp() {
        endUser = new EndUser();
        endUser.changeName("Test User");
        endUser.changePhone("010-1111-2222");
        endUser.changePassword("encodedPassword");
        endUser.changeActivated(true);
        // ID 직접 설정
        setId(endUser, 1L);

        party = new Party();
        setId(party, 100L);
        party.changeActivated(true);
        // party name은 builder 없이 생성자나 reflection 사용 불가하므로 생략

        endUserDto = new EndUserDto();
        endUserDto.setId(1L);
        endUserDto.setName("Test User");
        endUserDto.setPhone("010-1111-2222");
        endUserDto.setPartyIds(List.of(100L));
    }

    @Test
    void testSaveEndUser() {
        when(modelMapper.map(endUserDto, EndUser.class)).thenReturn(endUser);
        when(partyRepository.findById(100L)).thenReturn(Optional.of(party));
        when(endUserRepository.save(any())).thenReturn(endUser);

        Long id = endUserService.saveEndUser(endUserDto);

        assertEquals(1L, id);
        verify(pointRepository).save(any(Point.class));
    }

    @Test
    void testFindEndUserById() {
        Point point = new Point();
        setId(point, 200L);
        point.changeAssignedAmount(BigDecimal.ZERO);
        point.changeCurrentAmount(BigDecimal.ZERO);
        point.changeValidThru(LocalDateTime.now());

        // 연관 관계 설정
        point = setPartyAndEndUserToPoint(point, party, endUser);
        endUser.getPoints().add(point);

        when(endUserRepository.findById(1L)).thenReturn(Optional.of(endUser));
        when(modelMapper.map(endUser, EndUserDto.class)).thenReturn(endUserDto);

        EndUserDto result = endUserService.findEndUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(List.of(100L), result.getPartyIds());
        assertEquals(List.of(200L), result.getPointIds());
    }

    @Test
    void testUpdateEndUserPasswordById_Success() {
        when(endUserRepository.findById(1L)).thenReturn(Optional.of(endUser));

        boolean result = endUserService.updateEndUserPasswordById(1L, "newPass");

        assertTrue(result);
        verify(endUserRepository).save(endUser);
    }

    @Test
    void testUpdateEndUserPasswordById_WithOldPassword_Success() {
        when(endUserRepository.findById(1L)).thenReturn(Optional.of(endUser));
        when(passwordEncoder.matches("oldPass", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        boolean result = endUserService.updateEndUserPasswordById(1L, "oldPass", "newPass");

        assertTrue(result);
        verify(endUserRepository).save(endUser);
    }

    @Test
    void testUpdateEndUserPasswordById_WithOldPassword_Failure() {
        when(endUserRepository.findById(1L)).thenReturn(Optional.of(endUser));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        boolean result = endUserService.updateEndUserPasswordById(1L, "wrongPass", "newPass");

        assertFalse(result);
        verify(endUserRepository, never()).save(any());
    }

    @Test
    void testActivateEndUserById() {
        when(endUserRepository.findById(1L)).thenReturn(Optional.of(endUser));

        boolean result = endUserService.activateEndUserById(1L);

        assertTrue(result);
        verify(endUserRepository).save(endUser);
    }

    @Test
    void testDeactivateEndUserById() {
        when(endUserRepository.findById(1L)).thenReturn(Optional.of(endUser));

        boolean result = endUserService.deactivateEndUserById(1L);

        assertTrue(result);
        verify(endUserRepository).save(endUser);
    }

    @Test
    void testAssignPointToEndUserById() {
        when(endUserRepository.findById(1L)).thenReturn(Optional.of(endUser));
        when(partyRepository.findById(100L)).thenReturn(Optional.of(party));

        boolean result = endUserService.assignPointToEndUserById(1L, 100L, BigDecimal.TEN, LocalDateTime.now());

        assertTrue(result);
        verify(pointRepository).save(any(Point.class));
    }

    // ====== 헬퍼 메서드 ======

    private void setId(Object entity, Long idValue) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, idValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Point setPartyAndEndUserToPoint(Point point, Party party, EndUser endUser) {
        try {
            var partyField = Point.class.getDeclaredField("party");
            var userField = Point.class.getDeclaredField("endUser");
            partyField.setAccessible(true);
            userField.setAccessible(true);
            partyField.set(point, party);
            userField.set(point, endUser);
            return point;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}