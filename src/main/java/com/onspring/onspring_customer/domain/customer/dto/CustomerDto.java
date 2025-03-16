package com.onspring.onspring_customer.domain.customer.dto;

import lombok.Builder;
import lombok.Value;

import java.io.Serializable;

@Value
@Builder
public class CustomerDto implements Serializable {
    Long id;
    String name;
    String address;
    String phone;
    boolean isActivated;
}
