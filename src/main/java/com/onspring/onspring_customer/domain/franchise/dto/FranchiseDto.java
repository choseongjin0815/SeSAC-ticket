package com.onspring.onspring_customer.domain.franchise.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor(force = true)
public class FranchiseDto implements Serializable {
    Long id;

    @NotNull
    @Size(min = 1)
    String userName;

    String password;

    @NotNull
    @Size(min = 1)
    String name;

    @NotNull
    @Size(min = 1)
    String address;

    @NotNull
    @Size(min = 1)
    String phone;

    boolean isActivated;
}
