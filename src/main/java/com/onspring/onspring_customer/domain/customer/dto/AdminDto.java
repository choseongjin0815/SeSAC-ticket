package com.onspring.onspring_customer.domain.customer.dto;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
public class AdminDto implements Serializable {
    Long id;
    Long customerId;
    String userName;
    boolean isSuperAdmin;
}
