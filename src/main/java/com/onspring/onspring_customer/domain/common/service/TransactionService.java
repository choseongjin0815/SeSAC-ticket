package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    Long saveTransaction(TransactionDto transactionDto);

    TransactionDto findTransactionById(Long id);

    List<TransactionDto> findAllTransaction();

    List<TransactionDto> findTransactionByFranchiseId(Long franchiseId,
                                                LocalDateTime startDate,
                                                LocalDateTime endDate,
                                                String period);
    boolean cancelTransaction(Long franchiseId, Long transactionId);
}
