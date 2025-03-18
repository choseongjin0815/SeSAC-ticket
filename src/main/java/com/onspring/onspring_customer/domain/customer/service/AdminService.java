package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;

import java.util.List;

public interface AdminService {
    Long saveAdmin(AdminDto adminDto);

    AdminDto findAdminById(Long id);

    List<AdminDto> findAllAdmin();

    boolean changeAdminPasswordById(Long id, String password);

    boolean deleteAdminById(Long id);

    boolean deleteAllAdmin(List<Long> ids);
}
