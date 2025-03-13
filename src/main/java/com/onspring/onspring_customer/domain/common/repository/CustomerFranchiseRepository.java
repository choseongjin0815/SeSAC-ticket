package com.onspring.onspring_customer.domain.common.repository;

import com.onspring.onspring_customer.domain.common.entity.CustomerFranchise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerFranchiseRepository extends JpaRepository<CustomerFranchise, Long> {
}