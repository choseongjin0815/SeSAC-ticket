package com.onspring.onspring_customer.domain.auth.controller;

import com.onspring.onspring_customer.domain.auth.dto.LoginRequestDto;
import com.onspring.onspring_customer.domain.auth.dto.LoginResponseDto;
import com.onspring.onspring_customer.domain.auth.dto.RefreshTokenRequestDto;
import com.onspring.onspring_customer.domain.auth.dto.TokenResponseDto;
import com.onspring.onspring_customer.domain.auth.service.AuthenticationService;
import com.onspring.onspring_customer.global.util.jwt.JwtTokenProvider;
import com.onspring.onspring_customer.security.RefreshTokenService;
import com.onspring.onspring_customer.security.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@Tag(name = "인증 관련 API", description = "인증 관련 API 모음")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "가맹점 로그인", description = "가맹점 앱에서 사용할 로그인 API")
    @PostMapping("/api/franchise/login")
    public ResponseEntity<LoginResponseDto> franchiseLogin(@RequestBody LoginRequestDto request) {
        log.info("Franchise login request: {}", request);
        LoginResponseDto token = authenticationService.franchiseLogin(
                request.getUserName(),
                request.getPassword()
        );
        log.info("Franchise login response: {}", token);
        return ResponseEntity.ok(token);
    }


    @PostMapping("/api/franchise/logout")
    public ResponseEntity<?> franchiseLogout(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {

        log.info("Logout request: {}", request);

        String refreshToken = request.get("refreshToken");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }

        Long userId = SecurityUtil.getCurrentUserId();
        refreshTokenService.deleteRefreshToken("franchise" + userId);

        return ResponseEntity.ok("로그아웃 성공");
    }

    @Operation(summary = "사용자 로그인", description = "사용자 앱에서 사용할 로그인 API")
    @PostMapping("/api/user/login")
    public ResponseEntity<LoginResponseDto> userLogin(@RequestBody LoginRequestDto request) {
        log.info("User login request: {}", request);
        LoginResponseDto token = authenticationService.userLogin(
                request.getPhone(),
                request.getPassword()
        );
        log.info("User login response: {}", token);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/api/user/logout")
    public ResponseEntity<?> userLogout(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {

        log.info("Logout request: {}", request);

        String refreshToken = request.get("refreshToken");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }

        Long userId = SecurityUtil.getCurrentUserId();
        refreshTokenService.deleteRefreshToken("user" + userId);

        return ResponseEntity.ok("로그아웃 성공");
    }

    @Operation(summary = "토큰 재발급", description = "AccessToken만료시 사용할 토큰 재발급 API")
    @PostMapping("/api/token/refresh")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto request) {
        log.info("Refresh token request: {}", request);
        try {
            String newAccessToken = jwtTokenProvider.reissueAccessToken(request.getRefreshToken());
            log.info("Refresh token received: {}", newAccessToken);
            return ResponseEntity.ok(new TokenResponseDto(newAccessToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }



}