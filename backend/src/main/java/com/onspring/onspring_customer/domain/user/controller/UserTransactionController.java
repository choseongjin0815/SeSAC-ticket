package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.service.TransactionService;
import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.service.FranchiseService;
import com.onspring.onspring_customer.domain.user.service.EndUserService;
import com.onspring.onspring_customer.domain.user.service.PointService;
import com.onspring.onspring_customer.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/user/transactions")
@RestController
@Tag(name = "사용자의 결제 관련 API", description = "사용자의 결제 관련 API 모음")
public class UserTransactionController {

    private final TransactionService transactionService;
    private final PointService pointService;
    private final FranchiseService franchiseService;
    private final EndUserService endUserService;


    @Operation(summary = "사용자의 결제 내역 조회", description = "가장 최근 결제 내역 부터 조회되도록 조건을 주고 페이징을 적용한 결제 내역 조회")
    @GetMapping("")
    public ResponseEntity<Page<TransactionDto>> getUserTransactions(
            @PageableDefault(sort = "transactionTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info(pageable);
        Long userId = SecurityUtil.getCurrentUserId(); // 여기서 userId 추출
        Page<TransactionDto> result = transactionService.findTransactionByEndUserId(userId, pageable);
        return ResponseEntity.ok(result);
    }
    @Operation(summary = "사용자의 가맹점 결제", description = "결제 이후 포인트 차감하는 서비스 까지 동작")
    @PostMapping("/{franchiseId}/point/{pointId}")
    public ResponseEntity<Long> addTransaction(@PathVariable Long franchiseId,
                                               @PathVariable Long pointId,
                                               @RequestParam Long partyId,
                                               @RequestBody TransactionDto transactionDto) {

        Long userId = SecurityUtil.getCurrentUserId();

        log.info(transactionDto);
        log.info(franchiseId);
        FranchiseDto franchiseDto = franchiseService.findFranchiseById(franchiseId);

//        //MultiPart필드를 null로 초기화
//        franchiseDto.setFiles(new ArrayList<>());

        log.info("franchiseDto: " + franchiseDto);

        transactionDto.setFranchiseDto(franchiseDto);

        log.info(endUserService.findEndUserById(userId));
        transactionDto.setEndUserDto(endUserService.findEndUserById(userId));


        // 트랜잭션 저장 서비스 호출
        Long savedTransactionId = transactionService.saveTransaction(partyId, transactionDto);

        //결제 후 가용 포인트 처리 로직
        pointService.usePointOnPayment(pointId, transactionDto.getAmount());

        // 저장된 트랜잭션 ID 반환
        return ResponseEntity.ok(savedTransactionId);
    }
}
