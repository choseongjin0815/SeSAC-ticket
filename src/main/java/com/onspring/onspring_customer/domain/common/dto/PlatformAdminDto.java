package com.onspring.onspring_customer.domain.common.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class PlatformAdminDto implements Serializable {
    Long id;
    String userName;
}
