package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.PlatformAdminDto;
import com.onspring.onspring_customer.domain.common.entity.PlatformAdmin;
import com.onspring.onspring_customer.domain.common.repository.PlatformAdminRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlatformAdminServiceTest {
    @Mock
    private PlatformAdminRepository platformAdminRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PlatformAdminServiceImpl platformAdminService;

    private PlatformAdminDto platformAdminDto;
    private PlatformAdmin platformAdmin;

    @BeforeEach
    void setUp() {
        platformAdminDto = new PlatformAdminDto(1L, "admin1", "password", false, true);

        platformAdmin = new PlatformAdmin();
        platformAdmin.setId(1L);
        platformAdmin.setUserName("admin1");
        platformAdmin.setPassword("password");
        platformAdmin.setSuperAdmin(false);
        platformAdmin.setActivated(true);
    }

    @Test
    void testSavePlatformAdmin() {
        when(modelMapper.map(any(PlatformAdminDto.class), any())).thenReturn(platformAdmin);
        when(platformAdminRepository.save(any(PlatformAdmin.class))).thenReturn(platformAdmin);

        platformAdminService.savePlatformAdmin(platformAdminDto);

        verify(platformAdminRepository).save(platformAdmin);
        verify(modelMapper).map(platformAdminDto, PlatformAdmin.class);
    }

    @Test
    void testFindPlatformAdminById() {
        when(modelMapper.map(any(PlatformAdmin.class), any())).thenReturn(platformAdminDto);
        when(platformAdminRepository.findById(1L)).thenReturn(Optional.of(platformAdmin));

        PlatformAdminDto platformAdminDto1 = platformAdminService.findPlatformAdminById(1L);

        assertEquals(1L, platformAdminDto1.getId());
        assertEquals("admin1", platformAdminDto1.getUserName());
        assertEquals("password", platformAdminDto1.getPassword());
    }

    @Test
    void testFindPlatformAdminById_NotFound() {
        when(platformAdminRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> platformAdminService.findPlatformAdminById(1L));
    }

    @Test
    void testUpdatePlatformAdminPasswordById() {
        when(platformAdminRepository.findById(1L)).thenReturn(Optional.of(platformAdmin));

        boolean result = platformAdminService.updatePlatformAdminPasswordById(1L, "password1");

        assertTrue(result);

        verify(platformAdminRepository).save(any(PlatformAdmin.class));

        assertEquals("password1", platformAdmin.getPassword());
    }

    @Test
    void testActivatePlatformAdminById() {
        platformAdmin.setActivated(false);

        when(platformAdminRepository.findById(1L)).thenReturn(Optional.of(platformAdmin));

        boolean result = platformAdminService.activatePlatformAdminById(1L);

        assertTrue(result);

        verify(platformAdminRepository).save(any(PlatformAdmin.class));

        assertTrue(platformAdmin.isActivated());
    }

    @Test
    void testDeactivatedPlatformAdminById() {
        when(platformAdminRepository.findById(1L)).thenReturn(Optional.of(platformAdmin));

        boolean result = platformAdminService.deactivatePlatformAdminById(1L);

        assertTrue(result);

        verify(platformAdminRepository).save(any(PlatformAdmin.class));

        assertFalse(platformAdmin.isActivated());
    }
}
