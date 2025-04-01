package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.user.dto.PointResponseDto;
import com.onspring.onspring_customer.domain.user.service.PointService;
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
public class UserPointController {
    private final PointService pointService;

    @GetMapping("/points")
    public ResponseEntity<List<PointResponseDto>> getPointsByUserId() {
        Long userId = 2L; //테스트용 userId

        List<PointResponseDto> PointDetailList = pointService.getPointsByEndUserId(userId);

        log.info("PointList: {}", PointDetailList);

        return ResponseEntity.ok(PointDetailList);

    }
}
