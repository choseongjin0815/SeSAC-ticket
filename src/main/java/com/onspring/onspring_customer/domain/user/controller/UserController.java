package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.service.EndUserService;
import com.onspring.onspring_customer.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {
    private final EndUserService endUserService;

    @GetMapping("/info")
    public ResponseEntity<EndUserDto> getUserInfo() {
        Long userId = SecurityUtil.getCurrentUserId();

        log.info("get user info: {}", userId);

        EndUserDto endUserDto = endUserService.findEndUserById(userId);

        log.info("end user info: {}", endUserDto);

        return ResponseEntity.ok(endUserDto);
    }
}
