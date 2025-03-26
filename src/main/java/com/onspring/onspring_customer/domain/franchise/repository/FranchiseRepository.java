package com.onspring.onspring_customer.domain.franchise.repository;

import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

}