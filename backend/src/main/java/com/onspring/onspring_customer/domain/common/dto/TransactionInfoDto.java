package com.onspring.onspring_customer.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 거래내역 조회용 DTO
 */
@Getter
@AllArgsConstructor
public class TransactionInfoDto {

    // 거래 정보
    private Long id;
    private BigDecimal amount;
    private LocalDateTime transactionTime;

    // 소속한 그룹
    private String partyName;

    // 결제 사용자명
    private String username;

    // 거래 가맹점명
    private String franchiseName;

    // 결제 승인 여부, false시 결제 취소한 내역
    private Boolean isAccepted;

    // 정산 완료 여부
    private Boolean isClosed;
}