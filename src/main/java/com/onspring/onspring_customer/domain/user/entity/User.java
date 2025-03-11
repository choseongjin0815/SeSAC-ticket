package com.onspring.onspring_customer.domain.user.entity;

import com.onspring.onspring_customer.domain.common.entity.BaseEntity;
import com.onspring.onspring_customer.domain.common.entity.GroupUser;
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
@Table(name = "user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String phone;

    @NotNull
    private String isActivated;

    @OneToMany(mappedBy = "user")
    private Set<GroupUser> groupUsers = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Point> points = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Transaction> transactions = new HashSet<>();

}