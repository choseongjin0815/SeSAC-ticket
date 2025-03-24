package com.onspring.onspring_customer.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor(force = true)
public class EndUserDto implements Serializable {
    Long id;

    Long partyId;

    String password;

    @NotNull
    @Size(min = 1)
    String name;

    @NotNull
    @Size(min = 1)
    String phone;

    boolean isActivated;

    BigDecimal currentPoint;
}
