package com.onspring.onspring_customer.domain.user.entity;

import com.onspring.onspring_customer.domain.common.entity.BaseEntity;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Entity
@Table(name = "end_user")
@NoArgsConstructor
@AllArgsConstructor
public class EndUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    private String password;

    @NotNull
    private String name;

    @NotNull
    private String phone;

    @NotNull
    private boolean isActivated;

    @OneToMany(mappedBy = "endUser")
    private Set<Point> points = new HashSet<>();

    @OneToMany(mappedBy = "endUser")
    private Set<Transaction> transactions = new HashSet<>();

    public void changePassword(String password) {
        this.password = password;
    }

    public void changePhone(String phone) {
        this.phone = phone;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changeActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

}