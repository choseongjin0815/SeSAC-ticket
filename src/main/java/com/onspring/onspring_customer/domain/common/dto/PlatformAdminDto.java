package com.onspring.onspring_customer.domain.common.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

@Value
public class PlatformAdminDto implements Serializable {
    Long id;

    @NotNull
    @Size(min = 1)
    String userName;

    String password;

    boolean isSuperAdmin;

    boolean isActivated;
}
