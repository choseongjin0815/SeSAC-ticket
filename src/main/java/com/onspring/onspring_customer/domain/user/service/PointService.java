package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.user.dto.PointDto;
import com.onspring.onspring_customer.domain.user.dto.PointResponseDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface PointService {
    List<PointResponseDto> getPointsByEndUserId(Long endUserId);
    boolean usePointOnPayment(Long pointId, BigDecimal amount);

    boolean assignPointToEndUserById(Long endUserId, Long partyId, BigDecimal amount, LocalDateTime validThru);

    PointDto findAvailablePointByEndUserIdAndPartyId(Long endUserId, Long partyId);
}
