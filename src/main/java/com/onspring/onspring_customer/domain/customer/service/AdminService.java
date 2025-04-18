package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {
    Long saveAdmin(AdminDto adminDto);

    AdminDto findAdminById(Long id);

    List<AdminDto> findAllAdmin();

    Page<AdminDto> findAllAdminByQuery(Long customerId, String userName, boolean isActivated, Pageable pageable);

    boolean updateAdminPasswordById(Long id, String oldPassword, String newPassword);

    boolean activateAdminById(Long id);

    boolean deactivateAdminById(Long id);
}
