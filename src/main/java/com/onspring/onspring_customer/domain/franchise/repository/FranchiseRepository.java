package com.onspring.onspring_customer.domain.franchise.repository;

import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

    @Query("SELECT f FROM Franchise f " +
            "JOIN CustomerFranchise cf ON cf.franchise.id = f.id " +
            "JOIN Party p ON p.customer.id = cf.customer.id " +
            "JOIN PartyEndUser peu ON peu.party.customer.id = p.customer.id " +
            "WHERE peu.endUser.id = :endUserId")
    List<Franchise> findAllFranchiseByEndUserId(Long endUserId);

    Franchise findByUserName(String userName);
}