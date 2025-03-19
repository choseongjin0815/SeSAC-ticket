package com.onspring.onspring_customer.domain.user.service;

import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.entity.EndUser;

import java.util.List;

public interface EndUserService {
    Long saveEndUser(EndUserDto endUserDto);

    EndUserDto findEndUserById(Long id);

    List<EndUserDto> findAllEndUser();

    boolean updateEndUserPasswordById(Long id, String password);

    boolean activateEndUserById(Long id);

    boolean deactivateEndUserById(Long id);
}
