package com.onspring.onspring_customer.domain.customer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

@Value
public class AdminDto implements Serializable {
    Long id;

    Long customerId;

    String userName;

    String password;

    boolean isSuperAdmin;

    boolean isActivated;
}
