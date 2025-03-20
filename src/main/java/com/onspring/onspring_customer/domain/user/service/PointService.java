package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.user.dto.PointResponseDto;

import java.util.List;

public interface PointService {
    List<PointResponseDto> getPointsByEndUserId(Long endUserId);
}
