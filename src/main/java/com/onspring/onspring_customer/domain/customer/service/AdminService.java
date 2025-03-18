package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;

import java.util.List;

public interface AdminService {
    Long saveAdmin(AdminDto adminDto);

    AdminDto findAdminById(Long id);

    List<AdminDto> findAllAdmin();

    boolean changeAdminPasswordById(Long id, String password);

    boolean activateAdminById(Long id);

    boolean deactivateAdminById(Long id);
}
