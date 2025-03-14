package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;

import java.util.List;

public interface AdminService {
    Long createAdmin(AdminDto adminDto);

    AdminDto findAdminById(Long id);

    List<AdminDto> findAllAdmin();

    boolean updateAdmin(AdminDto adminDto);

    boolean deleteAdminById(Long id);

    boolean deleteAllAdmin(List<Long> ids);
}
