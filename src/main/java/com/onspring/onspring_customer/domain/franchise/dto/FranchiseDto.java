package com.onspring.onspring_customer.domain.franchise.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor(force = true)
public class FranchiseDto implements Serializable {
    Long id;
    String userName;

    String password;

    String name;
    String address;
    String phone;

    boolean isActivated;
}
