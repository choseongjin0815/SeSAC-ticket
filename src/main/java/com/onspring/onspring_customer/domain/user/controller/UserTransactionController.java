package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.service.TransactionService;
import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.service.FranchiseService;
import com.onspring.onspring_customer.domain.user.service.EndUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/user/transactions")
@RestController
public class UserTransactionController {

    private final TransactionService transactionService;
    private final FranchiseService franchiseService;
    private final EndUserService endUserService;

    @GetMapping("")
    public ResponseEntity<List<TransactionDto>> getUserTransactions() {
        Long id = 1L; //스프링 시큐리티 적용 전이므로 테스트용 아이디
        List<TransactionDto> transactionDtoList = transactionService.findTransactionByEndUserId(id);

        return ResponseEntity.ok(transactionDtoList);
    }

    @PostMapping("/{franchiseId}")
    public ResponseEntity<Long> addTransaction(@PathVariable Long franchiseId,
                                               @RequestBody TransactionDto transactionDto) {
        Long userId = 2L; // 테스트용 아이디

        log.info(transactionDto);
        log.info(franchiseId);
        FranchiseDto franchiseDto = franchiseService.findFranchiseById(franchiseId);
        //MultiPart필드를 null로 초기화
        franchiseDto.setFiles(new ArrayList<>());

        log.info("franchiseDto: " + franchiseDto);

        transactionDto.setFranchiseDto(franchiseDto);

        log.info(endUserService.findEndUserById(userId));
        transactionDto.setEndUserDto(endUserService.findEndUserById(userId));


        // 트랜잭션 저장 서비스 호출
        Long savedTransactionId = transactionService.saveTransaction(transactionDto);

        // 저장된 트랜잭션 ID 반환
        return ResponseEntity.ok(savedTransactionId);
    }
}
