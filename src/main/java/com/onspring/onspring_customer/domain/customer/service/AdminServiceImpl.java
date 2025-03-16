package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;
import com.onspring.onspring_customer.domain.customer.entity.Admin;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.repository.AdminRepository;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Setter
@Getter
@Service
public class AdminServiceImpl implements AdminService {
    private AdminRepository adminRepository;
    private CustomerRepository customerRepository;

    @Override
    public Long createAdmin(AdminDto adminDto) {
        Customer customer = customerRepository.findById(adminDto.getCustomerId())
                .orElseThrow();
        Admin admin = Admin.builder()
                .customer(customer)
                .userName(adminDto.getUserName())
                .isSuperAdmin(adminDto.isSuperAdmin())
                .build();

        return adminRepository.save(admin)
                .getId();
    }

    @Override
    public AdminDto findAdminById(Long id) {
        Optional<Admin> result = adminRepository.findById(id);
        Admin admin = result.orElseThrow();

        return AdminDto.builder()
                .id(admin.getId())
                .customerId(admin.getCustomer()
                        .getId())
                .userName(admin.getUserName())
                .isSuperAdmin(admin.isSuperAdmin())
                .build();
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
