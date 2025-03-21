package com.onspring.onspring_customer.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SettlmentSummaryDto {
    private int year;
    private int month;
    private long totalTransactions;
    private BigDecimal totalAmount;
}
