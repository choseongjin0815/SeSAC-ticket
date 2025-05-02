package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.user.dto.PointResponseDto;
import com.onspring.onspring_customer.domain.user.service.PointService;
import com.onspring.onspring_customer.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "사용자의 포인트 관련 API", description = "사용자의 포인트 관련 API")
public class UserPointController {
    private final PointService pointService;

    @Operation(summary = "사용자의 그룹별 포인트 리스트 조회", description = "사용자의 그룹별 포인트 리스트 조회")
    @GetMapping("/points")
    public ResponseEntity<List<PointResponseDto>> getPointsByUserId() {
        Long userId = SecurityUtil.getCurrentUserId();

        List<PointResponseDto> PointDetailList = pointService.getPointsByEndUserId(userId);

        log.info("PointList: {}", PointDetailList);

        return ResponseEntity.ok(PointDetailList);

    }
}
