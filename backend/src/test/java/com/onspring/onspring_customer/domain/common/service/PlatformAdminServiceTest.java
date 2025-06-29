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

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    void setUp() throws Exception {
        platformAdminDto = new PlatformAdminDto(1L, "admin1", "password", false, true);
        platformAdmin = new PlatformAdmin();

        // 테스트를 위한 필드 주입 (setter가 없어서 reflection 사용)
        Field idField = PlatformAdmin.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(platformAdmin, 1L);

        Field userNameField = PlatformAdmin.class.getDeclaredField("userName");
        userNameField.setAccessible(true);
        userNameField.set(platformAdmin, "admin1");

        Field passwordField = PlatformAdmin.class.getDeclaredField("password");
        passwordField.setAccessible(true);
        passwordField.set(platformAdmin, "password");

        Field isSuperAdminField = PlatformAdmin.class.getDeclaredField("isSuperAdmin");
        isSuperAdminField.setAccessible(true);
        isSuperAdminField.set(platformAdmin, false);

        Field isActivatedField = PlatformAdmin.class.getDeclaredField("isActivated");
        isActivatedField.setAccessible(true);
        isActivatedField.set(platformAdmin, true);
    }

    @Test
    void testSavePlatformAdmin() {
        when(modelMapper.map(any(PlatformAdminDto.class), eq(PlatformAdmin.class))).thenReturn(platformAdmin);
        when(platformAdminRepository.save(any(PlatformAdmin.class))).thenReturn(platformAdmin);

        Long result = platformAdminService.savePlatformAdmin(platformAdminDto);

        assertEquals(1L, result);
        verify(platformAdminRepository).save(platformAdmin);
        verify(modelMapper).map(platformAdminDto, PlatformAdmin.class);
    }

    @Test
    void testFindPlatformAdminById() {
        when(modelMapper.map(any(PlatformAdmin.class), eq(PlatformAdminDto.class))).thenReturn(platformAdminDto);
        when(platformAdminRepository.findById(1L)).thenReturn(Optional.of(platformAdmin));

        PlatformAdminDto result = platformAdminService.findPlatformAdminById(1L);

        assertEquals("admin1", result.getUserName());
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
        when(platformAdminRepository.findById(1L)).thenReturn(Optional.of(platformAdmin));

        boolean result = platformAdminService.activatePlatformAdminById(1L);

        assertTrue(result);
        verify(platformAdminRepository).save(any(PlatformAdmin.class));
        assertTrue(platformAdmin.isActivated());
    }

    @Test
    void testDeactivatePlatformAdminById() {
        when(platformAdminRepository.findById(1L)).thenReturn(Optional.of(platformAdmin));

        boolean result = platformAdminService.deactivatePlatformAdminById(1L);

        assertTrue(result);
        verify(platformAdminRepository).save(any(PlatformAdmin.class));
        assertFalse(platformAdmin.isActivated());
    }

    @Test
    void testDeactivatePlatformAdminById_FailIfSuperAdmin() throws Exception {
        setField(platformAdmin, "isSuperAdmin", true);
        when(platformAdminRepository.findById(1L)).thenReturn(Optional.of(platformAdmin));

        boolean result = platformAdminService.deactivatePlatformAdminById(1L);

        assertFalse(result);
        verify(platformAdminRepository, never()).save(any());
    }

    @Test
    void testDeletePlatformAdminById() {
        when(platformAdminRepository.findById(1L)).thenReturn(Optional.of(platformAdmin));

        boolean result = platformAdminService.deletePlatformAdminById(1L);

        assertTrue(result);
        verify(platformAdminRepository).delete(platformAdmin);
    }

    @Test
    void testDeletePlatformAdminById_FailIfSuperAdmin() throws Exception {
        setField(platformAdmin, "isSuperAdmin", true);
        when(platformAdminRepository.findById(1L)).thenReturn(Optional.of(platformAdmin));

        boolean result = platformAdminService.deletePlatformAdminById(1L);

        assertFalse(result);
        verify(platformAdminRepository, never()).delete(any());
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}