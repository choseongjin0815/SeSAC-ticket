package com.onspring.onspring_customer.global.auth.controller;

import com.onspring.onspring_customer.global.auth.dto.LoginRequestDto;
import com.onspring.onspring_customer.global.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/api/franchise/login")
    public ResponseEntity<String> franchiseLogin(@RequestBody LoginRequestDto request) {
        String token = authenticationService.franchiseLogin(
                request.getUserName(),
                request.getPassword()
        );
        return ResponseEntity.ok(token);
    }

    @PostMapping("/api/user/login")
    public ResponseEntity<String> userLogin(@RequestBody LoginRequestDto request) {
        String token = authenticationService.userLogin(
                request.getPhone(),
                request.getPassword()
        );
        return ResponseEntity.ok(token);
    }


//    static class LoginRequest {
//        private String userName;
//        private String phone;
//        private String password;
//
//        // Getters and setters
//        public String getUserName() { return userName; }
//        public String getPhone() { return phone; }
//        public String getPassword() { return password; }
//    }
}
