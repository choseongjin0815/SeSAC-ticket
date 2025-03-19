package com.onspring.onspring_customer.domain.user.dto;

import lombok.Value;

import java.io.Serializable;
import java.math.BigDecimal;

@Value
public class EndUserDto implements Serializable {
    Long id;

    Long partyId;

    String password;

    String name;

    String phone;

    boolean isActivated;

    BigDecimal currentPoint;
}
