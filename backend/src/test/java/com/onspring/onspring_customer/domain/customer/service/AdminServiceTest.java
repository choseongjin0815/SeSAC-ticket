package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;
import com.onspring.onspring_customer.domain.customer.entity.Admin;
import com.onspring.onspring_customer.domain.customer.repository.AdminRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private AdminRepository adminRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private AdminServiceImpl adminService;

    private Admin admin;
    private AdminDto adminDto;

    @BeforeEach
    void setUp() throws Exception {
        admin = new Admin();
        setField(admin, "id", 1L);
        setField(admin, "userName", "admin1");
        setField(admin, "password", "encodedPassword");
        setField(admin, "isSuperAdmin", false);
        setField(admin, "isActivated", true);

        adminDto = new AdminDto();
        adminDto.setId(1L);
        adminDto.setUserName("admin1");
        adminDto.setPassword("encodedPassword");
        adminDto.setActivated(true);
        adminDto.setSuperAdmin(false);
    }

    @Test
    void testSaveAdmin() {
        when(modelMapper.map(adminDto, Admin.class)).thenReturn(admin);
        when(adminRepository.save(admin)).thenReturn(admin);

        Long result = adminService.saveAdmin(adminDto);

        assertEquals(1L, result);
        verify(adminRepository).save(admin);
    }

    @Test
    void testFindAdminById() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(modelMapper.map(admin, AdminDto.class)).thenReturn(adminDto);

        AdminDto result = adminService.findAdminById(1L);

        assertEquals("admin1", result.getUserName());
    }

    @Test
    void testFindAdminById_NotFound() {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminService.findAdminById(1L));
    }

    @Test
    void testFindAllAdmin() {
        when(adminRepository.findAll()).thenReturn(List.of(admin));
        when(modelMapper.map(admin, AdminDto.class)).thenReturn(adminDto);

        List<AdminDto> result = adminService.findAllAdmin();

        assertEquals(1, result.size());
        assertEquals("admin1", result.get(0).getUserName());
    }

    @Test
    void testUpdateAdminPasswordById_success() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        boolean result = adminService.updateAdminPasswordById(1L, "oldPassword", "newPassword");

        assertTrue(result);
        verify(adminRepository).save(admin);
    }

    @Test
    void testUpdateAdminPasswordById_wrongOldPassword() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(passwordEncoder.matches("wrongOld", "encodedPassword")).thenReturn(false);

        boolean result = adminService.updateAdminPasswordById(1L, "wrongOld", "newPassword");

        assertFalse(result);
        verify(adminRepository, never()).save(any());
    }

    @Test
    void testActivateAdminById() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        boolean result = adminService.activateAdminById(1L);

        assertTrue(result);
        verify(adminRepository).save(admin);
    }

    @Test
    void testDeactivateAdminById_success() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        boolean result = adminService.deactivateAdminById(1L);

        assertTrue(result);
        verify(adminRepository).save(admin);
    }

    @Test
    void testDeactivateAdminById_failIfSuperAdmin() throws Exception {
        setField(admin, "isSuperAdmin", true);
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        boolean result = adminService.deactivateAdminById(1L);

        assertFalse(result);
        verify(adminRepository, never()).save(any());
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}