package com.onspring.onspring_customer.domain.common.dto;

import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import lombok.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Value
public class PartyEndUserDto implements Serializable {
    Long partyId;
    String partyName;

    List<EndUserDto> endUserList = new ArrayList<>();
}
