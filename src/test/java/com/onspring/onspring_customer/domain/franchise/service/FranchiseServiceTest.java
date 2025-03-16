package com.onspring.onspring_customer.domain.franchise.service;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class FranchiseServiceTest {
    @Autowired
    private FranchiseService franchiseService;

    @Test
    public void findFranchiseByIdTest() {
        Long id = 1L;
        FranchiseDto result = franchiseService.findFranchiseById(id);
        System.out.println("Found FranchiseDto: " + result);
        assert result != null;
    }
}
