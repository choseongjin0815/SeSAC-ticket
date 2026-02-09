package com.onspring.onspring_customer.domain.common.repository;

import com.onspring.onspring_customer.domain.common.dto.TransactionInfoDto;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
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
            "ANd t.isClosed = true " +
            "ORDER BY t.id DESC" )
    List<Transaction> findTransactionsByFranchiseIdAndDateRangeAndClosed(
            @Param("franchiseId") Long franchiseId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);


    Page<Transaction> findByEndUserId(Long endUserId, Pageable pageable);

    @Query("SELECT MONTH(t.transactionTime) AS month, YEAR(t.transactionTime) AS year, " +
            "COUNT(t) AS totalTransactions, SUM(t.amount) AS totalAmount " +
            "FROM Transaction t " +
            "WHERE t.isClosed = true " +
            "AND t.franchise.id = :franchiseId " +
            "GROUP BY YEAR(t.transactionTime), MONTH(t.transactionTime) " +
            "ORDER BY YEAR(t.transactionTime) DESC, MONTH(t.transactionTime) DESC")
    List<Object[]> getMonthlyTransactionSummary(@Param("franchiseId") Long franchiseId);

    /**
     * 승인된 거래내역 조회
     * - N+1 문제 해결: JOIN으로 연관 엔티티 함께 조회
     * - over-fetching 방지: 필요한 컬럼만 SELECT
     * - count 쿼리 최적화: 불필요한 JOIN 제거
     */
    @Query(
            value = """
            SELECT new com.onspring.onspring_customer.domain.common.dto.TransactionInfoDto(
                t.id,
                t.amount,
                t.transactionTime,
                p.name,
                e.name,
                f.name,
                t.isAccepted,
                t.isClosed
            )
            FROM Transaction t
            JOIN t.franchise f
            JOIN t.endUser e
            JOIN t.party p
            JOIN p.customer c
            JOIN c.admins a
            WHERE a.id = :adminId
              AND t.isAccepted = true
            ORDER BY t.transactionTime DESC
            """,
            countQuery = """
            SELECT COUNT(t.id)
            FROM Transaction t
            JOIN t.party p
            JOIN p.customer c
            JOIN c.admins a
            WHERE a.id = :adminId
              AND t.isAccepted = true
            """
    )
    Page<TransactionInfoDto> findAllAcceptedTransaction(
            @Param("adminId") Long adminId,
            Pageable pageable
    );
    Page<TransactionInfoDto> findAllAcceptedAndNotClosedTransaction(
            @Param("adminId") Long adminId,
            Pageable pageable
    );

    @Transactional
    @Modifying
    @Query("update Transaction t set t.isClosed = false where t.id in ?1 and t.isAccepted = true and t.isClosed = " +
           "false")
    int updateIsClosedByIdInAndIsAcceptedTrueAndIsClosedFalse(@NonNull Collection<Long> ids);
}