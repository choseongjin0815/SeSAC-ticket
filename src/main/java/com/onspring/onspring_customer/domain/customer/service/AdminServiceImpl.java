package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;
import com.onspring.onspring_customer.domain.customer.repository.AdminRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Getter
@Service
public class AdminServiceImpl implements AdminService {
    private AdminRepository adminRepository;

    @Override
    public Long createAdmin(AdminDto adminDto) {
        return 0L;
    }

    @Override
    public AdminDto findAdminById(Long id) {
        return null;
    }

    @Override
    public List<AdminDto> findAllAdmin() {
        return List.of();
    }

    @Override
    public boolean updateAdmin(AdminDto adminDto) {
        return false;
    }

    @Override
    public boolean deleteAdminById(Long id) {
        return false;
    }

    @Override
    public boolean deleteAllAdmin(List<Long> ids) {
        return false;
    }
}
