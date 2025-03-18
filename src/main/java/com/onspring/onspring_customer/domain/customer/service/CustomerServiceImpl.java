package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.customer.dto.CustomerDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class CustomerServiceImpl implements CustomerService {
    private CustomerRepository customerRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long saveCustomer(CustomerDto customerDto) {
        Customer customer = Customer.builder()
                .name(customerDto.getName())
                .address(customerDto.getAddress())
                .phone(customerDto.getPhone())
                .isActivated(customerDto.isActivated())
                .build();

        return customerRepository.save(customer)
                .getId();
    }

    @Override
    public CustomerDto findCustomerById(Long id) {
        Optional<Customer> result = customerRepository.findById(id);
        Customer customer = result.orElseThrow();

        return CustomerDto.builder()
                .name(customer.getName())
                .address(customer.getAddress())
                .phone(customer.getPhone())
                .isActivated(customer.isActivated())
                .build();
    }

    @Override
    public List<CustomerDto> findAllCustomer() {
        return List.of();
    }

    @Override
    public boolean updateCustomer(CustomerDto customerDto) {
        Optional<Customer> result = customerRepository.findById(customerDto.getId());
        Customer customer = result.orElseThrow();

        customer.setName(customerDto.getName());
        customer.setAddress(customerDto.getAddress());
        customer.setPhone(customerDto.getPhone());
        customer.setActivated(customerDto.isActivated());
        customerRepository.save(customer);

        return true;
    }

    @Override
    public boolean deleteCustomerById(Long id) {
        return false;
    }
}
