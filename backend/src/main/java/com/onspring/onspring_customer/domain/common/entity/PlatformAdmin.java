package com.onspring.onspring_customer.domain.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Entity
@Table(name = "platform_admin")
public class PlatformAdmin extends BaseEntity {
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
    private boolean isSuperAdmin;

    @NotNull
    private boolean isActivated;

}