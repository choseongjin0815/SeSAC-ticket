package com.onspring.onspring_customer.domain.franchise.repository;

import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

    @Query("SELECT DISTINCT f FROM Franchise f " +
            "JOIN CustomerFranchise cf ON cf.franchise = f " +
            "JOIN Party p ON p.customer = cf.customer " +
            "JOIN PartyEndUser peu ON peu.party = p " +
            "WHERE peu.endUser.id = :endUserId")
    Page<Franchise> findAllFranchiseByEndUserId(@Param("endUserId") Long endUserId, Pageable pageable);

    Franchise findByUserName(String userName);
}