package com.onspring.onspring_customer.domain.franchise.entity;

import com.onspring.onspring_customer.domain.common.entity.BaseEntity;
import com.onspring.onspring_customer.domain.common.entity.CustomerFranchise;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "franchise")
public class Franchise extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String userName;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @NotNull
    @Column(unique = true)
    private String address;

    @NotNull
    @Column(unique = true)
    private String phone;

    @NotNull
    private boolean isActivated;

    @OneToMany(mappedBy = "franchise")
    private Set<CustomerFranchise> customerFranchises = new HashSet<>();

    @OneToMany(mappedBy = "franchise")
    private Set<Transaction> transactions = new HashSet<>();

}