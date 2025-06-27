package com.onspring.onspring_customer.domain.common.entity;

import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@ToString
@Entity
@Table(name = "transaction")
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "franchise_id")
    private Franchise franchise;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private Party party;

    @ManyToOne
    @JoinColumn(name = "end_user_id")
    private EndUser endUser;

    @NotNull
    private LocalDateTime transactionTime;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private boolean isAccepted;

    @NotNull
    private boolean isClosed;

    public Transaction(Franchise franchise, EndUser endUser, BigDecimal amount, boolean isClosed, Party party) {
        this.franchise = franchise;
        this.endUser = endUser;
        this.amount = amount;
        this.isAccepted = true;
        this.isClosed = isClosed;
        this.party = party;
        this.transactionTime = LocalDateTime.now();
    }

    public void cancelTransaction() {
        this.isAccepted = false;
    }

    public void closeTransaction() {
        this.isClosed = true;
    }

}