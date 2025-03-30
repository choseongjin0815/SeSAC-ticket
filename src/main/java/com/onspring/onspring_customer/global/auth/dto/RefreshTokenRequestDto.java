package com.onspring.onspring_customer.global.auth.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RefreshTokenRequestDto {
    private String refreshToken;
}
