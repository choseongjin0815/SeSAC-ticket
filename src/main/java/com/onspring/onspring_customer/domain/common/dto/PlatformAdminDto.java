package com.onspring.onspring_customer.domain.common.dto;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
public class PlatformAdminDto implements Serializable {
    Long id;
    String userName;
}
