package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.CustomerDto;

import java.util.List;

public interface CustomerService {
    Long saveCustomer(CustomerDto customerDto);

    CustomerDto findCustomerById(Long id);

    List<CustomerDto> findAllCustomer();

    boolean updateCustomer(CustomerDto customerDto);

    boolean deleteCustomerById(Long id);
}
