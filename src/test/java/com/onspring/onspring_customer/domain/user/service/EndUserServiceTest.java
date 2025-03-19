package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EndUserServiceTest {

    @Mock
    private EndUserRepository endUserRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private EndUserServiceImpl endUserService;

    private EndUser endUser;
    private EndUserDto endUserDto;

    @BeforeEach
    void setUp() {
        endUserDto = new EndUserDto(
                20000L,
                20000L,
                "sesac1234",
                "testName",
                "010-1234-5678",
                true,
                new BigDecimal(5000)
        );

        endUser = new EndUser();
        endUser.setId(20000L);
        endUser.setPassword("sesac1234");
        endUser.setName("testName");
        endUser.setPhone("010-1234-5678");
        endUser.setActivated(true);
    }

    @Test
    void testFindEndUserById() {
        // given
        when(endUserRepository.findById(20000L)).thenReturn(Optional.of(endUser));
        when(modelMapper.map(endUser, EndUserDto.class)).thenReturn(endUserDto);

        // when
        EndUserDto result = endUserService.findEndUserById(20000L);

        // then
        verify(endUserRepository).findById(20000L);
        verify(modelMapper).map(endUser, EndUserDto.class);

        assertEquals(20000L, result.getId());
        assertEquals("testName", result.getName());
        assertEquals("010-1234-5678", result.getPhone());
    }
}