package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TransactionServiceTest {
    @Autowired
    private TransactionService transactionService; // 실제 서비스 주입

    /**
     * 특정 가맹점의 결제내역 period 조회 테스트
     */
    @Test
    public void testFindTransactionByFranchiseIdWithPeriod() {
        Long franchiseId = 1L;
        String period = "최근1주";  // 예: 최근 1주간


        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        switch (period) {
            case "오늘":
                startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);  // 오늘의 시작 시점
                endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);  // 오늘의 끝 시점
                break;
            case "최근1주":
                startDate = LocalDateTime.now().minusWeeks(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                System.out.println(startDate + " - " + endDate);
                break;
            case "최근2주":
                startDate = LocalDateTime.now().minusWeeks(2).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                break;
            case "최근3주":
                startDate = LocalDateTime.now().minusWeeks(3).withHour(0).withMinute(0).withSecond(0).withNano(0);
                endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                break;
            default:
                throw new IllegalArgumentException("Invalid period value: " + period);
        }

        List<TransactionDto> transactions = transactionService.findTransactionByFranchiseId(franchiseId, startDate, endDate, null);
        System.out.println(transactions);
        assertNotNull(transactions);
        assertFalse(transactions.isEmpty(), "No transactions found for the given period.");
    }

    /**
     * 특정 가맹점의 결제내역 기간 조회 테스트
     */
    @Test
    public void testFindTransactionByFranchiseIdWithDateRange() {
        Long franchiseId = 1L;

        LocalDateTime startDate = LocalDateTime.now().minusDays(3).withHour(0).withMinute(0).withSecond(0).withNano(0);  // 예: 3일전
        LocalDateTime endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);  // 예: 오늘

        List<TransactionDto> transactions = transactionService.findTransactionByFranchiseId(franchiseId, startDate, endDate, null);

        System.out.println(transactions);
        assertNotNull(transactions);
        assertFalse(transactions.isEmpty(), "No transactions found for the given date range.");
    }
}
