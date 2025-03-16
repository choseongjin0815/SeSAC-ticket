package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.PlatformAdminDto;
import com.onspring.onspring_customer.domain.common.entity.PlatformAdmin;
import com.onspring.onspring_customer.domain.common.repository.PlatformAdminRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Setter
@Getter
@Service
public class PlatformAdminServiceImpl implements PlatformAdminService {
    private PlatformAdminRepository platformAdminRepository;

    @Override
    public Long savePlatformAdmin(PlatformAdminDto platformAdminDto) {
        PlatformAdmin platformAdmin = PlatformAdmin.builder()
                .userName(platformAdminDto.getUserName())
                .build();

        return platformAdminRepository.save(platformAdmin)
                .getId();
    }

    @Override
    public PlatformAdminDto findPlatformAdminById(Long id) {
        Optional<PlatformAdmin> result = platformAdminRepository.findById(id);
        PlatformAdmin platformAdmin = result.orElseThrow();

        return PlatformAdminDto.builder()
                .id(platformAdmin.getId())
                .userName(platformAdmin.getUserName())
                .build();
    }

    @Override
    public List<PlatformAdminDto> findAllPlatformAdmin() {
        return List.of();
    }

    @Override
    public boolean updatePlatformAdmin(PlatformAdminDto platformAdminDto) {
        return false;
    }

    @Override
    public boolean deletePlatformAdminByID(Long id) {
        return false;
    }

    @Override
    public boolean deleteAllPlatformAdmin(List<Long> ids) {
        return false;
    }

}
