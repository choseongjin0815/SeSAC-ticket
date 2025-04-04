package com.onspring.onspring_customer.domain.user.controller;

import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.service.EndUserService;
import com.onspring.onspring_customer.global.auth.dto.PasswordUpdateRequest;
import com.onspring.onspring_customer.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @PutMapping("/password")
    public ResponseEntity<String> updateUserPassword(
            @RequestBody PasswordUpdateRequest request) {

        log.info("User password update request: {}", request);
        Long userId = SecurityUtil.getCurrentUserId();

        boolean success = endUserService.updateEndUserPasswordById(userId, request.getOldPassword(), request.getNewPassword());

        if (success) {
            log.info("User password updated successfully");
            return ResponseEntity.ok("password updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("password update failed");
        }
    }
}
