package com.onspring.onspring_customer.domain.customer.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class AdminDto implements Serializable {
    Long id;
    String userName;
    boolean isSuperAdmin;
}
