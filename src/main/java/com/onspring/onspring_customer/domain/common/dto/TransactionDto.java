package com.onspring.onspring_customer.domain.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto implements Serializable {
    Long id;
//    FranchiseDto franchiseDto;
//    EndUserDto endUserDto;
    Long userId;
    Long franchiseId;
    LocalDateTime transactionTime;
    BigDecimal amount;
    boolean isAccepted;
    boolean isClosed;
}
