package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    Long saveTransaction(TransactionDto transactionDto);

    TransactionDto findTransactionById(Long id);

    List<TransactionDto> findAllTransaction();

    TransactionDto findTransactionByFranchiseId(Long franchiseId,
                                                LocalDate startDate,
                                                LocalDate endDate,
                                                String period);
}
