package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    Long saveTransaction(TransactionDto transactionDto);

    // 미정산 내역 일괄 처리 메소드 추가
    List<Long> saveTransactions(List<Long> transactionIds);

    TransactionDto findTransactionById(Long id);

    List<TransactionDto> findAllTransaction();

    List<TransactionDto> findTransactionByFranchiseId(Long franchiseId,
                                                LocalDateTime startDate,
                                                LocalDateTime endDate,
                                                String period);

    List<TransactionDto> findSettlementByFranchiseId(Long franchiseId, String month, String period, String customStartDate, String customEndDate);

    boolean cancelTransaction(Long franchiseId, Long transactionId);
}
