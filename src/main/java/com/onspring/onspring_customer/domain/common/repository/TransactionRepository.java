package com.onspring.onspring_customer.domain.common.repository;

import com.onspring.onspring_customer.domain.common.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 특정 기간 동안의 트랜잭션을 조회하는 JPQL 쿼리
    @Query("SELECT t FROM Transaction t WHERE t.franchise.id = :franchiseId AND t.transactionTime BETWEEN :startDate AND :endDate")
    List<Transaction> findTransactionsByFranchiseIdAndDateRange(@Param("franchiseId") Long franchiseId,
                                                                @Param("startDate") LocalDateTime startDate,
                                                                @Param("endDate") LocalDateTime endDate);

    // 특정 period에 맞는 트랜잭션을 조회하는 JPQL 쿼리
    @Query("SELECT t FROM Transaction t WHERE t.franchise.id = :franchiseId AND t.transactionTime BETWEEN :startDate AND :endDate")
    List<Transaction> findTransactionsByFranchiseIdAndPeriod(@Param("franchiseId") Long franchiseId,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);
}