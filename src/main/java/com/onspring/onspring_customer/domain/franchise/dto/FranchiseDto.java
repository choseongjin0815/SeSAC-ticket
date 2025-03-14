package com.onspring.onspring_customer.domain.franchise.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class FranchiseDto implements Serializable {
    Long id;
    String name;
    String address;
    String phone;
}
