package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.PlatformAdminDto;

import java.util.List;

public interface PlatformAdminService {
    Long savePlatformAdmin(PlatformAdminDto platformAdminDto);

    PlatformAdminDto findPlatformAdminById(Long id);

    List<PlatformAdminDto> findAllPlatformAdmin();

    boolean updatePlatformAdmin(PlatformAdminDto platformAdminDto);

    boolean deletePlatformAdminByID(Long id);

    boolean deleteAllPlatformAdmin(List<Long> ids);
}
