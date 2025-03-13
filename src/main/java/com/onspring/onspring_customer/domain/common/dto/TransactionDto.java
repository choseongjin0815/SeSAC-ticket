package com.onspring.onspring_customer.domain.common.dto;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class TransactionDto implements Serializable {
    Long id;
    FranchiseDto franchiseDto;
    EndUserDto endUserDto;
    LocalDateTime transactionTime;
    BigDecimal amount;
    boolean isAccepted;
    boolean isClosed;
}
