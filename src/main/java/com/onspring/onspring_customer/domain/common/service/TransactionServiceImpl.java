package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.SettlmentSummaryDto;
import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import com.onspring.onspring_customer.domain.common.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;

    @Override
    public Long saveTransaction(TransactionDto transactionDto) {
        return 0L;
    }

    @Override
    public TransactionDto findTransactionById(Long id) {
        return null;
    }

    @Override
    public List<TransactionDto> findAllTransaction() {
        return List.of();
    }

    /**
     * 특정 가맹점에 대한 결제내역 조회 (이후 페이징등 추가 고려 필요)
     *
     * @param franchiseId   조회할 가맹점의 id
     * @param startDate     기간 조회의 시작일
     * @param endDate       기간 조회의 종료일
     * @param period        오늘, 최근 1주, 최근 2주, 최근 3주
     * @return  해당 가맹점의 기간 필터링을 적용한 TransactionDto 반환
     */
    @Override
    public List<TransactionDto> findTransactionByFranchiseId(Long franchiseId, LocalDateTime startDate, LocalDateTime endDate, String period) {
        // 기간이 주어지면 period를 사용하고, startDate와 endDate는 무시
        if (period != null) {
            switch (period) {
                case "오늘":
                    startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);  // 오늘의 시작 시점
                    endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);  // 오늘의 끝 시점
                    break;
                case "최근1주":
                    startDate = LocalDateTime.now().minusWeeks(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
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
        }

        List<Transaction> transactions;

        // startDate와 endDate가 모두 주어졌을 때 해당 기간 내의 트랜잭션 조회
        if (startDate != null && endDate != null) {
            transactions = transactionRepository.findTransactionsByFranchiseIdAndDateRange(franchiseId, startDate, endDate);
        } else {
            // period를 사용하여 트랜잭션 조회
            transactions = transactionRepository.findTransactionsByFranchiseIdAndPeriod(franchiseId, startDate, endDate);
        }

        // ModelMapper를 사용하여 List<Transaction>을 List<TransactionDto>로 변환
        return transactions.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class))
                .collect(Collectors.toList());
    }

    /**
     * 사용자의 결제 내역을 조회
     * @param userId 사용자의 id
     * @return 해당하는 TransactionDto의 List 객체
     */
    @Override
    public List<TransactionDto> findTransactionByEndUserId(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId는 null일 수 없습니다.");
        }
        List<Transaction> transactionList = transactionRepository.findByEndUserIdOrderByTransactionTimeDesc(userId);

        List<TransactionDto> transactionDtoList = transactionList.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class))
                .collect(Collectors.toList());

        return transactionDtoList;
    }

    /**
     * 특정 가맹점의 해당 월에 대한 정산내역 조회 (이후 페이징등 추가 고려 필요)
     *
     * @param franchiseId     조회할 가맹점의 id
     * @param month           정산 월
     * @param period          오늘, 최근1주, 최근2주, 최근3주 등
     * @param customStartDate 조회 시작일
     * @param customEndDate   조회 종료일

     * @return  해당 가맹점의 기간 필터링을 적용한 TransactionDto 반환
     */
    @Override
    public List<TransactionDto> findSettlementByFranchiseId(Long franchiseId, String month, String period, String customStartDate, String customEndDate) {
        // 기간을 처리하기 위한 startDate, endDate 선언
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;

        // period가 주어졌을 경우 해당 기간에 맞는 startDate와 endDate 계산
        if (period != null) {
            switch (period) {
                case "오늘":
                    startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);  // 오늘의 시작 시점
                    endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);  // 오늘의 끝 시점
                    break;
                case "최근1주":
                    startDate = LocalDateTime.now().minusWeeks(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
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
        } else if (month != null) {
            // 월이 주어졌을 경우 해당 월의 startDate와 endDate 계산
            if (!month.matches("\\d{4}-\\d{2}")) {
                throw new IllegalArgumentException("Invalid month format. Use 'YYYY-MM' format.");
            }
            startDate = LocalDateTime.parse(month + "-01T00:00:00");  // 해당 월의 첫 번째 날짜 (00:00:00)
            endDate = startDate.plusMonths(1).minusNanos(1);  // 해당 월의 마지막 날짜 (23:59:59.999999999)
        } else if (customStartDate != null && customEndDate != null) {
            // customStartDate와 customEndDate가 주어졌을 경우
            try {
                startDate = LocalDateTime.parse(customStartDate + "T00:00:00");
                endDate = LocalDateTime.parse(customEndDate + "T23:59:59.999999999");
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use 'YYYY-MM-DD' format for start and end date.");
            }
        }

        // startDate와 endDate가 설정되지 않은 경우 예외 처리
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both start date and end date must be provided.");
        }

        // isClosed가 true인 트랜잭션을 조회
        List<Transaction> transactions = transactionRepository.findTransactionsByFranchiseIdAndDateRangeAndClosed(
                franchiseId, startDate, endDate);


        return transactions.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class))
                .collect(Collectors.toList());
    }


    /**
     * 특정 가맹점의 월별 정산 합산 조회
     * @param franchiseId   가맹점의 id
     * @return  월별 정산의 총 거래량, 금액을 담은 SettlmentSummaryDto
     */
    @Override
    public List<SettlmentSummaryDto> getMonthlySettlementSummaries(Long franchiseId) {
        List<Object[]> results = transactionRepository.getMonthlyTransactionSummary(franchiseId);
        List<SettlmentSummaryDto> summaries = new ArrayList<>();
        for (Object[] result : results) {
            int month = (int) result[0];
            int year = (int) result[1];
            long totalTransactions = (long) result[2];
            BigDecimal totalAmount = (BigDecimal) result[3];

            summaries.add(new SettlmentSummaryDto(year, month, totalTransactions, totalAmount));
        }
        return summaries;
    }


    /**
     * 특정 가맹점의 결제취소 처리
     * 정산 완료된 결제 내역은 취소 불가
     * @param franchiseId   가맹점의 id
     * @param transactionId 결제 id
     * @return  취소 성공여부를 담은 boolean
     */
    @Override
    public boolean cancelTransaction(Long franchiseId, Long transactionId) {
        // 트랜잭션을 찾음
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        // 가맹점 ID가 맞는지 확인
        if (!transaction.getFranchise().getId().equals(franchiseId)) {
            throw new RuntimeException("Franchise ID mismatch");
        }

        // 이미 취소된 트랜잭션인지 확인
        if (!transaction.isAccepted()) {
            throw new RuntimeException("Transaction already cancelled");
        }

        // 트랜잭션이 닫혀 있으면 취소 불가능
        if (transaction.isClosed()) {
            throw new RuntimeException("Cannot cancel a closed transaction");
        }

        // 취소 처리: isAccepted를 false로 변경
        transaction.setAccepted(false);
        transactionRepository.save(transaction);

        return true;
    }
}
