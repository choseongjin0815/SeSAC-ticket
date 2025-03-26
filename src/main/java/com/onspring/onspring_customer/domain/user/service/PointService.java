package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.user.dto.PointResponseDto;

import java.math.BigDecimal;
import java.util.List;

public interface PointService {
    List<PointResponseDto> getPointsByEndUserId(Long endUserId);
    boolean usePointOnPayment(Long pointId, BigDecimal amount);
}
