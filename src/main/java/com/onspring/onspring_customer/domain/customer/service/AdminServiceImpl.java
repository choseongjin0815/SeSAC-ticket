package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.AdminDto;
import com.onspring.onspring_customer.domain.customer.entity.Admin;
import com.onspring.onspring_customer.domain.customer.repository.AdminRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, ModelMapper modelMapper) {
        this.adminRepository = adminRepository;
        this.modelMapper = modelMapper;
    }

    private Admin getAdmin(Long id) {
        Optional<Admin> result = adminRepository.findById(id);

        return result.orElseThrow(() -> new EntityNotFoundException("Admin with ID " + id + " not found"));
    }

    @Override
    public Long saveAdmin(AdminDto adminDto) {
        log.info("Saving admin with user name {}", adminDto.getUserName());

        Admin admin = modelMapper.map(adminDto, Admin.class);
        Long id = adminRepository.save(admin)
                .getId();

        log.info("Successfully saved admin with user name {}", adminDto.getUserName());

        return id;
    }

    @Override
    public AdminDto findAdminById(Long id) {
        Admin admin = getAdmin(id);

        return modelMapper.map(admin, AdminDto.class);
    }

    @Override
    public List<AdminDto> findAllAdmin() {
        return adminRepository.findAll()
                .stream()
                .map(element -> modelMapper.map(element, AdminDto.class))
                .toList();
    }

    @Override
    public boolean updateAdminPasswordById(Long id, String password) {
        log.info("Updating password for admin with ID {}", id);

        Admin admin = getAdmin(id);

        admin.setPassword(password);
        adminRepository.save(admin);

        log.info("Successfully updated password for admin with ID {}", id);

        return true;
    }

    @Override
    public boolean activateAdminById(Long id) {
        log.info("Activating admin with ID {}", id);

        Admin admin = getAdmin(id);

        admin.setActivated(true);
        adminRepository.save(admin);

        log.info("Successfully activated admin with ID {}", id);

        return true;
    }

    @Override
    public boolean deactivateAdminById(Long id) {
        log.info("Deactivating admin with ID {}", id);

        Admin admin = getAdmin(id);

        if (admin.isSuperAdmin()) {
            log.warn("Attempted to deactivate a super admin with ID {}", id);

            return false;
        }

        admin.setActivated(false);
        adminRepository.save(admin);

        log.info("Successfully deactivated admin with ID {}", id);

        return true;
    }
}
