package com.onspring.onspring_customer.domain.customer.entity;

import com.onspring.onspring_customer.domain.common.entity.BaseEntity;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.user.entity.Point;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "party")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Party extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @NotNull
    @Column(unique = true)
    private String name;

    private LocalDateTime period;

    private BigDecimal amount;

    private LocalTime allowedTimeStart;

    private LocalTime allowedTimeEnd;

    private Long validThru;

    @NotNull
    private boolean sunday;

    @NotNull
    private boolean monday;

    @NotNull
    private boolean tuesday;

    @NotNull
    private boolean wednesday;

    @NotNull
    private boolean thursday;

    @NotNull
    private boolean friday;

    @NotNull
    private boolean saturday;

    private BigDecimal maximumAmount;

    private Long maximumTransaction;

    @NotNull
    private boolean isActivated;

    @OneToMany(mappedBy = "party")
    private List<Point> points = new ArrayList<>();

    @OneToMany(mappedBy = "party")
    private List<Transaction> transactions = new ArrayList<>();

//    public Party(String name,
//                 LocalDateTime period,
//                 BigDecimal amount,
//                 LocalTime allowedTimeStart,
//                 LocalTime allowedTimeEnd,
//                 Long validThru,
//                 boolean sunday,
//                 boolean monday,
//                 boolean tuesday,
//                 boolean wednesday,
//                 boolean thursday,
//                 boolean friday,
//                 boolean saturday,
//                 BigDecimal maximumAmount,
//                 Long maximumTransaction) {
//        this.period = period;
//        this.amount = amount;
//        this.allowedTimeStart = allowedTimeStart;
//        this.allowedTimeEnd = allowedTimeEnd;
//        this.validThru = validThru;
//        this.sunday = sunday;
//        this.monday = monday;
//        this.tuesday = tuesday;
//        this.wednesday = wednesday;
//        this.thursday = thursday;
//        this.friday = friday;
//        this.saturday = saturday;
//        this.maximumAmount = maximumAmount;
//        this.maximumTransaction = maximumTransaction;
//    }

    public void changeCustomer(Customer customer) {
        this.customer = customer;
    }

    public void changeActivated(boolean isActivated) {
        this.isActivated = isActivated;
    }

    public void updatePartyInfo(PartyDto dto) {
        this.name = dto.getName();
        this.period = dto.getPeriod();
        this.amount = dto.getAmount();
        this.allowedTimeStart = dto.getAllowedTimeStart();
        this.allowedTimeEnd = dto.getAllowedTimeEnd();
        this.validThru = dto.getValidThru();
        this.sunday = dto.isSunday();
        this.monday = dto.isMonday();
        this.tuesday = dto.isTuesday();
        this.wednesday = dto.isWednesday();
        this.thursday = dto.isThursday();
        this.friday = dto.isFriday();
        this.saturday = dto.isSaturday();
        this.maximumAmount = dto.getMaximumAmount();
        this.maximumTransaction = dto.getMaximumTransaction();
    }

}