package com.onspring.onspring_customer.domain.customer.repository;

import com.onspring.onspring_customer.domain.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}