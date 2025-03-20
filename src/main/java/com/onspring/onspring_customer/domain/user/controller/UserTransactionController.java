package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/user/transactions")
@RestController
public class UserTransactionController {

    private final TransactionService transactionService;

    @GetMapping("")
    public ResponseEntity<List<TransactionDto>> getUserTransactions() {
        Long id = 1L; //스프링 시큐리티 적용 전이므로 테스트용 아이디
        List<TransactionDto> transactionDtoList = transactionService.findTransactionByEndUserId(id);

        return ResponseEntity.ok(transactionDtoList);
    }
}
