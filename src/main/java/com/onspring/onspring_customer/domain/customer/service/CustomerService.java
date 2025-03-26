package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.CustomerDto;

import java.util.List;

public interface CustomerService {
    Long saveCustomer(CustomerDto customerDto);

    CustomerDto findCustomerById(Long id);

    List<CustomerDto> findAllCustomer();

    boolean updateCustomer(CustomerDto customerDto);

    Long addFranchiseToCustomer(Long customerId, Long franchiseId);

    boolean deleteFranchiseFromCustomer(Long id);

    boolean activateCustomerById(Long id);

    boolean deactivateCustomerById(Long id);
}
