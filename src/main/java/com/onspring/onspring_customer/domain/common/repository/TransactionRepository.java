package com.onspring.onspring_customer.domain.common.repository;

import com.onspring.onspring_customer.domain.common.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 특정 기간 동안의 트랜잭션을 조회하는 JPQL 쿼리
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.franchise.id = :franchiseId " +
            "AND t.transactionTime BETWEEN :startDate " +
            "AND :endDate " +
            "ORDER BY t.id DESC")
    List<Transaction> findTransactionsByFranchiseIdAndDateRange(@Param("franchiseId") Long franchiseId,
                                                                @Param("startDate") LocalDateTime startDate,
                                                                @Param("endDate") LocalDateTime endDate);

    // 특정 period에 맞는 트랜잭션을 조회하는 JPQL 쿼리
    @Query("SELECT t FROM Transaction t " +
            "WHERE t.franchise.id = :franchiseId " +
            "AND t.transactionTime BETWEEN :startDate " +
            "AND :endDate " +
            "ORDER BY t.id DESC")
    List<Transaction> findTransactionsByFranchiseIdAndPeriod(@Param("franchiseId") Long franchiseId,
                                                             @Param("startDate") LocalDateTime startDate,
                                                             @Param("endDate") LocalDateTime endDate);


    @Query("SELECT t FROM Transaction t WHERE t.franchise.id = :franchiseId " +
            "AND t.transactionTime BETWEEN :startDate AND :endDate " +
            "ORDER BY t.id DESC")
    List<Transaction> findTransactionsByFranchiseIdAndDateRangeAndClosed(
            @Param("franchiseId") Long franchiseId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    List<Transaction> findByEndUserIdOrderByTransactionTimeDesc(Long endUserId);

    @Query("SELECT MONTH(t.transactionTime) AS month, YEAR(t.transactionTime) AS year, " +
            "COUNT(t) AS totalTransactions, SUM(t.amount) AS totalAmount " +
            "FROM Transaction t " +
            "WHERE t.isClosed = true " +
            "AND t.franchise.id = :franchiseId " +
            "GROUP BY YEAR(t.transactionTime), MONTH(t.transactionTime) " +
            "ORDER BY YEAR(t.transactionTime) DESC, MONTH(t.transactionTime) DESC")
    List<Object[]> getMonthlyTransactionSummary(@Param("franchiseId") Long franchiseId);


    List<Transaction> findByIsClosed(boolean closed);

    @Query("select t from Transaction t where t.isAccepted = true and t.isClosed = false")
    Page<Transaction> findByIsAcceptedTrueAndIsClosedFalse(Pageable pageable);
}