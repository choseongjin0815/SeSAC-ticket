package com.onspring.onspring_customer.domain.franchise.service;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FranchiseService {
    Long saveFranchise(FranchiseDto franchiseDto);

    FranchiseDto findFranchiseById(Long id);

    Page<FranchiseDto> findFranchiseListByUserId(Long userId, Pageable pageable);

    List<FranchiseDto> findAllFranchise();

    boolean updateFranchise(Long id, FranchiseDto franchiseDto);

    boolean updateFranchisePassword(Long id, String oldPassword, String newPassword);

    boolean activateFranchiseById(Long id);

    boolean deactivateFranchiseById(Long id);

    boolean updateMenuImage(FranchiseDto franchiseDto);

    boolean deleteFranchiseById(Long id);
}
