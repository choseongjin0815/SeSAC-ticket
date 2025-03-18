package com.onspring.onspring_customer.domain.customer.service;

import com.onspring.onspring_customer.domain.common.repository.CustomerFranchiseRepository;
import com.onspring.onspring_customer.domain.customer.dto.CustomerDto;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.repository.CustomerRepository;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final FranchiseRepository franchiseRepository;
    private final CustomerFranchiseRepository customerFranchiseRepository;
    private final ModelMapper modelMapper;

    private Customer getCustomer(Long id) {
        Optional<Customer> result = customerRepository.findById(id);

        return result.orElseThrow(() -> new EntityNotFoundException("Customer with ID " + id + " not found"));
    }

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, FranchiseRepository franchiseRepository,
                               CustomerFranchiseRepository customerFranchiseRepository, ModelMapper modelMapper) {
        this.customerRepository = customerRepository;
        this.franchiseRepository = franchiseRepository;
        this.customerFranchiseRepository = customerFranchiseRepository;
        this.modelMapper = modelMapper;
    }

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
        Customer customer = getCustomer(id);

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
        Customer customer = getCustomer(customerDto.getId());
        Customer customer = getCustomer(customerId);

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
