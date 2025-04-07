package com.onspring.onspring_customer.domain.common.dto;

import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Value
public class TransactionArchiveDto implements Serializable {
    Long id;
    FranchiseDto franchiseDto;
    Long transactionCount;
    BigDecimal amountSum;
    LocalDate duration;
}
