package com.onspring.onspring_customer.domain.franchise.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Embeddable
public class FranchiseMenuImages {

    private String fileName;

    private int ord;

    public FranchiseMenuImages(String fileName, int ord) {
        this.fileName = fileName;
        this.ord = ord;
    }

}
