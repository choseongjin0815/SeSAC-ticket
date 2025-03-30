package com.onspring.onspring_customer.global.auth.controller;

import com.onspring.onspring_customer.global.auth.dto.LoginRequestDto;
import com.onspring.onspring_customer.global.auth.dto.LoginResponseDto;
import com.onspring.onspring_customer.global.auth.dto.RefreshTokenRequestDto;
import com.onspring.onspring_customer.global.auth.dto.TokenResponseDto;
import com.onspring.onspring_customer.global.auth.service.AuthenticationService;
import com.onspring.onspring_customer.global.util.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/api/franchise/login")
    public ResponseEntity<LoginResponseDto> franchiseLogin(@RequestBody LoginRequestDto request) {
        log.info("Franchise login request: {}", request);
        LoginResponseDto token = authenticationService.franchiseLogin(
                request.getUserName(),
                request.getPassword()
        );
        return ResponseEntity.ok(token);
    }

    @PostMapping("/api/user/login")
    public ResponseEntity<LoginResponseDto> userLogin(@RequestBody LoginRequestDto request) {
        LoginResponseDto token = authenticationService.userLogin(
                request.getPhone(),
                request.getPassword()
        );
        return ResponseEntity.ok(token);
    }

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