package com.onspring.onspring_customer.domain.customer.repository;

import com.onspring.onspring_customer.domain.customer.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, Long> {
}