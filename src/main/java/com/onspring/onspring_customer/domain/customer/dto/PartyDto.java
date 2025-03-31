package com.onspring.onspring_customer.domain.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartyDto implements Serializable {
    Long id;
    Long customerId;
    String name;
    LocalDateTime period;
    BigDecimal amount;
    LocalTime allowedTimeStart;
    LocalTime allowedTimeEnd;
    Long validThru;
    boolean sunday;
    boolean monday;
    boolean tuesday;
    boolean wednesday;
    boolean thursday;
    boolean friday;
    boolean saturday;
    BigDecimal maximumAmount;
    Long maximumTransaction;
    boolean isActivated;
}
