package com.onspring.onspring_customer.domain.franchise.service;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseServiceTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JPAQueryFactory queryFactory;

    @InjectMocks
    private FranchiseServiceImpl franchiseService;

    private Franchise franchise;
    private FranchiseDto franchiseDto;

    @BeforeEach
    void setUp() {
        franchise = new Franchise(1L, "Test Franchise", "Seoul", "010-1234-5678", "Test Desc");
        franchise.changePassword("encodedPassword");
        franchise.changeActivated(true);

        franchiseDto = FranchiseDto.builder()
                .id(1L)
                .name("Test Franchise")
                .address("Seoul")
                .phone("010-1234-5678")
                .description("Test Desc")
                .build();
    }

    @Test
    void testSaveFranchise() {
        when(modelMapper.map(franchiseDto, Franchise.class)).thenReturn(franchise);
        when(franchiseRepository.save(any())).thenReturn(franchise);

        Long result = franchiseService.saveFranchise(franchiseDto);

        assertEquals(1L, result);
        verify(franchiseRepository).save(franchise);
    }

    @Test
    void testFindFranchiseById() {
        when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));

        FranchiseDto expectedDto = FranchiseDto.builder()
                .id(1L)
                .name("Test Franchise")
                .address("Seoul")
                .phone("010-1234-5678")
                .description("Test Desc")
                .build();

        // entityToDto가 직접 호출되므로 stub 필요 없음
        FranchiseDto result = franchiseService.findFranchiseById(1L);

        assertEquals(expectedDto.getId(), result.getId());
    }

    @Test
    void testUpdateFranchiseFields() {
        franchiseService.updateFranchiseFields(franchise, franchiseDto);

        assertEquals("Test Franchise", franchise.getName());
        assertEquals("Seoul", franchise.getAddress());
        assertEquals("010-1234-5678", franchise.getPhone());
        assertEquals("Test Desc", franchise.getDescription());
    }

    @Test
    void testUpdateFranchise() {
        when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));

        boolean result = franchiseService.updateFranchise(1L, franchiseDto);

        assertTrue(result);
        verify(franchiseRepository).save(franchise);
    }

    @Test
    void testUpdateFranchisePassword_Success() {
        when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));
        when(passwordEncoder.matches("oldPass", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        boolean result = franchiseService.updateFranchisePassword(1L, "oldPass", "newPass");

        assertTrue(result);
        verify(franchiseRepository).save(franchise);
    }

    @Test
    void testUpdateFranchisePassword_Failure() {
        when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));
        when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

        boolean result = franchiseService.updateFranchisePassword(1L, "wrongPass", "newPass");

        assertFalse(result);
        verify(franchiseRepository, never()).save(any());
    }

    @Test
    void testUpdateMenuImage() {
        when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));

        franchiseDto.setUploadFileNames(List.of("img1.jpg", "img2.jpg"));

        boolean result = franchiseService.updateMenuImage(franchiseDto);

        assertTrue(result);
        verify(franchiseRepository).save(franchise);
    }

    @Test
    void testActivateFranchiseById() {
        when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));

        boolean result = franchiseService.activateFranchiseById(1L);

        assertTrue(result);
        verify(franchiseRepository).save(franchise);
    }

    @Test
    void testDeactivateFranchiseById() {
        when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));

        boolean result = franchiseService.deactivateFranchiseById(1L);

        assertTrue(result);
        verify(franchiseRepository).save(franchise);
    }

    @Test
    void testActivateFranchiseByIdList() {
        when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));

        List<Boolean> result = franchiseService.activateFranchiseById(List.of(1L));

        assertEquals(1, result.size());
        assertTrue(result.get(0));
    }

    @Test
    void testDeactivateFranchiseByIdList() {
        when(franchiseRepository.findById(1L)).thenReturn(Optional.of(franchise));

        List<Boolean> result = franchiseService.deactivateFranchiseById(List.of(1L));

        assertEquals(1, result.size());
        assertTrue(result.get(0));
    }
}