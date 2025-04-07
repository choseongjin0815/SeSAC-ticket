package com.onspring.onspring_customer.domain.customer.repository;

import com.onspring.onspring_customer.domain.customer.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PartyRepository extends JpaRepository<Party, Long> {

    @Query("SELECT peu.party FROM PartyEndUser peu WHERE peu.endUser.id = :endUserId ORDER BY peu.createdAt ASC")
    List<Party> findOldestPartyByEndUserId(@Param("endUserId") Long endUserId);
}