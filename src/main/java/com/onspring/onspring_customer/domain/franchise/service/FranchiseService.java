package com.onspring.onspring_customer.domain.franchise.service;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;

import java.util.List;

public interface FranchiseService {
    Long saveFranchise(FranchiseDto franchiseDto);

    FranchiseDto findFranchiseById(Long id);

    List<FranchiseDto> findFranchiseListByUserId(Long userId);

    List<FranchiseDto> findAllFranchise();

    boolean updateFranchise(Long id, FranchiseDto franchiseDto);

    boolean activateFranchiseById(Long id);

    boolean deactivateFranchiseById(Long id);
    boolean updateMenuImage(FranchiseDto franchiseDto);

    boolean deleteFranchiseById(Long id);
}
