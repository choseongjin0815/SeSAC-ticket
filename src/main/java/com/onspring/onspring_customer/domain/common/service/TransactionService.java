package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;

import java.util.List;

public interface TransactionService {
    Long saveTransaction(TransactionDto transactionDto);

    TransactionDto findTransactionById(Long id);

    List<TransactionDto> findAllTransaction();
}
