package com.onspring.onspring_customer.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class PointDto implements Serializable {
    Long id;

    Long partyId;

    Long endUserId;

    @NotNull
    BigDecimal amount;

    LocalDateTime validThru;
}
