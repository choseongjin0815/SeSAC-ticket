package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.service.FranchiseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/franchises")
public class UserFranchiseController {

    private final FranchiseService franchiseService;

    @GetMapping("/franchises")
    public ResponseEntity<List<FranchiseDto>> getAllFranchises() {
        Long userId = 1L; //테스트용 사용자 id

        List<FranchiseDto> franchiseDtoList = franchiseService.findFranchiseListByUserId(userId);

        return ResponseEntity.ok(franchiseDtoList);
    }

}
