package com.onspring.onspring_customer.domain.franchise.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class FranchiseMenuImages {

    private String fileName;

    private int ord;

}
