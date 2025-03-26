package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;
import com.onspring.onspring_customer.domain.customer.entity.Admin;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.entity.QAdmin;
import com.onspring.onspring_customer.domain.customer.repository.AdminRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    private AdminRepository adminRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private JPAQueryFactory queryFactory;

    @Mock
    private JPAQuery<Admin> query;

    @InjectMocks
    private AdminServiceImpl adminService;

    private AdminDto adminDto;
    private Admin admin;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();

        adminDto = new AdminDto(1L, 1L, "admin", "password", true, false);

        admin = new Admin();
        admin.setId(1L);
        admin.setCustomer(customer);
        admin.setUserName("admin");
        admin.setPassword("password");
        admin.setActivated(true);
        admin.setSuperAdmin(false);
    }

    @Test
    void testSaveAdmin() {
        when(modelMapper.map(any(AdminDto.class), any())).thenReturn(admin);
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        Long savedAdminId = adminService.saveAdmin(adminDto);

        verify(adminRepository).save(admin);
        verify(modelMapper).map(adminDto, Admin.class);

        assertEquals(admin.getId(), savedAdminId);
    }

    @Test
    void testFindAdminById() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(modelMapper.map(any(Admin.class), any())).thenReturn(adminDto);

        AdminDto foundAdmin = adminService.findAdminById(1L);

        assertNotNull(foundAdmin);
        assertEquals(adminDto.getId(), foundAdmin.getId());
    }

    @Test
    void testFindAdminById_NotFound() {
        when(adminRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> adminService.findAdminById(1L));
    }

    @Test
    void testFindAllAdminByQuery() {
        Long customerId = 1L;
        String userName = "admin";
        boolean isActivated = true;
        Pageable pageable = PageRequest.of(0, 10);

        List<Admin> admins = Collections.singletonList(admin);

        when(queryFactory.selectFrom(QAdmin.admin)).thenReturn(query);
        when(query.where(any(Predicate.class))).thenReturn(query);
        when(query.offset(anyLong())).thenReturn(query);
        when(query.limit(anyLong())).thenReturn(query);
        when(query.fetch()).thenReturn(admins);

        when(modelMapper.map(admin, AdminDto.class)).thenReturn(adminDto);

        Page<AdminDto> result = adminService.findAllAdminByQuery(customerId, userName, isActivated, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(adminDto, result.getContent()
                .get(0));
        verify(queryFactory).selectFrom(QAdmin.admin);
        verify(query).where(QAdmin.admin.customer.id.eq(customerId));
        verify(query).where(QAdmin.admin.userName.containsIgnoreCase(userName));
        verify(query).where(QAdmin.admin.isActivated.eq(isActivated));
    }

    @Test
    void testUpdateAdminPasswordById() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        boolean result = adminService.updateAdminPasswordById(1L, "password1");

        assertTrue(result);

        verify(adminRepository).save(admin);

        assertEquals("password1", admin.getPassword());
    }

    @Test
    void testActivateAdminById() {
        admin.setActivated(false);

        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        boolean result = adminService.activateAdminById(1L);

        assertTrue(result);

        verify(adminRepository).save(admin);

        assertTrue(admin.isActivated());
    }

    @Test
    void testDeactivateAdminById() {
        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        boolean result = adminService.deactivateAdminById(1L);

        assertTrue(result);

        verify(adminRepository).save(admin);

        assertFalse(admin.isActivated());
    }

    @Test
    void testDeactivateAdminById_SuperAdmin() {
        admin.setSuperAdmin(true);

        when(adminRepository.findById(1L)).thenReturn(Optional.of(admin));

        boolean result = adminService.deactivateAdminById(1L);

        assertFalse(result);

        verify(adminRepository, never()).save(any(Admin.class));
    }
}
