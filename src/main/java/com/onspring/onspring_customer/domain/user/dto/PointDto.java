package com.onspring.onspring_customer.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PointDto implements Serializable {
    Long id;

    @NotNull
    Long partyId;

    @NotNull
    Long endUserId;

    @NotNull
    BigDecimal assignedAmount;

    @NotNull
    BigDecimal currentAmount;

    LocalDateTime validThru;

}
