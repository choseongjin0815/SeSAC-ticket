package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.SettlmentSummaryDto;
import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    Long saveTransaction(TransactionDto transactionDto);

    List<TransactionDto> findAllTransaction();

    List<TransactionDto> findTransactionByFranchiseId(Long franchiseId,
                                                LocalDateTime startDate,
                                                LocalDateTime endDate,
                                                String period);
    Page<TransactionDto> findTransactionByEndUserId(Long userId, Pageable pageable);

    List<TransactionDto> findSettlementByFranchiseId(Long franchiseId,
                                                     String month,
                                                     String period,
                                                     LocalDateTime startDate,
                                                     LocalDateTime endDate);

    List<SettlmentSummaryDto> getMonthlySettlementSummaries(Long franchiseId);

    boolean cancelTransaction(Long franchiseId, Long transactionId);
}
