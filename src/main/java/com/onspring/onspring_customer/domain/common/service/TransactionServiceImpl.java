package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import com.onspring.onspring_customer.domain.common.repository.TransactionRepository;
import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final ModelMapper modelMapper;


    // 선택된 미정산 거래 내역(단 한개)가 정산으로 저장됨 isClosed = False -> True => Figma 홈_정산관리_정산확인
    @Override
    public Long saveTransaction(TransactionDto transactionDto) {
        log.info("Saving transaction with ID: {}", transactionDto.getId());

        Transaction existingTransaction = transactionRepository.findById(transactionDto.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionDto.getId()));

        // 현재 월의 첫날 00시 00분 00초 000밀리초로 시작 시간 설정.
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        // 다음 달의 첫날에서 1나노초를 뺀 값으로 끝 시간 설정 (현재 월의 마지막 순간)
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusNanos(1);
        // 이미 정산 처리된 거래인지 확인
        if (existingTransaction.isClosed()) {
            throw new RuntimeException("Transaction is already closed with ID: " + transactionDto.getId());
        }

        // 정산 처리할 가맹점 ID를 DTO에서 가져오기
        Long franchiseId = transactionDto.getId();
        // 정산 처리로 상태 변경 (isClosed = true)
        existingTransaction.setClosed(true);

        // 해당 가맹점의 현재 월에 대한 미정산내역(isClosed=false) 조회
        List<Transaction> transactions = transactionRepository.findTransactionsByFranchiseIdAndDateRangeAndClosedStatus(
                franchiseId, startOfMonth, endOfMonth, false
        );
        // 변경된 내용 저장
        Transaction savedTransaction = transactionRepository.save(existingTransaction);
        log.info("Transaction successfully closed: {}", savedTransaction.getId());

        // 미정산 내역이 없으면 예외
        if (transactions.isEmpty()) {
            throw new RuntimeException("No open transactions found for franchise ID: " + franchiseId + " in current month");
        }
        return savedTransaction.getId();

    }

    // 선택된 미정산 거래 내역(여러 개)가 정산으로 저장됨 isClosed = False -> True => Figma 홈_정산관리_정산확인
    @Override
    @Transactional
    public List<Long> saveTransactions(List<Long> transactionIds) {
        log.info("Processing multiple transactions: {}", transactionIds);

        LocalDateTime closedAt = LocalDateTime.now();
        for (Transaction transaction : transactions) {
            transaction.setClosed(true);
        }

        transactionRepository.saveAll(transactions);
        List<Long> processedIds = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Long id : transactionIds) {
            try {
                Transaction transaction = transactionRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + id));

                // 이미 정산 처리된 거래는 건너뜀
                if (transaction.isClosed()) {
                    log.warn("Transaction is already closed with ID: {}", id);
                    continue;
                }

                // 정산 처리로 상태 변경 (isClosed = true)
                transaction.setClosed(true);


                // 변경된 내용 저장
                Transaction savedTransaction = transactionRepository.save(transaction);
                log.info("Transaction successfully closed: {}", savedTransaction.getId());

                processedIds.add(savedTransaction.getId());
            } catch (Exception e) {
                log.error("Error processing transaction with ID {}: {}", id, e.getMessage());
                // 개별 트랜잭션 처리 실패 시에도 나머지 작업은 계속 진행
            }
        }

        return Long.valueOf(transactions.size());
        log.info("Successfully closed {} transactions", processedIds.size());
        return processedIds;
    }

    @Override
    public TransactionDto findTransactionById(Long id) {
        return null;
    }



    // 정산 처리가 완료된(isClosed=true) 내역을 가맹점 기준으로 한달 간격으로 정리해서 요약 표시 => Figma 
    @Override
    public List<TransactionDto> findMonthlySettlementSummary() {
        log.info("Finding monthly settlement summary by franchise");

        return modelMapper.map(transaction, TransactionDto.class);
        // 정산 완료된(isClosed=true) 모든 트랜잭션 조회
        List<Transaction> closedTransactions = transactionRepository.findByIsClosed(true);

    }
        if (closedTransactions.isEmpty()) {
            log.info("No closed transactions found");
            return new ArrayList<>();
        }

    // 거래된 모든 것들 중 false인 (정산되지 않는 것만) 찾아서 리스트로 보여주기
        // 가맹점 ID와 월별로 트랜잭션 그룹화
        Map<String, List<Transaction>> groupedTransactions = closedTransactions.stream()
                .collect(Collectors.groupingBy(transaction -> {
                    // 가맹점 ID와 연월을 조합한 키 생성 (예: "franchiseId-2025-02")
                    LocalDateTime date = transaction.getTransactionTime();
                    String yearMonth = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                    return transaction.getFranchise().getId() + "-" + yearMonth;
                }));

        // 그룹화된 트랜잭션으로부터 가맹점별 월간 요약 정보 생성
        List<TransactionDto> summaries = new ArrayList<>();

        groupedTransactions.forEach((key, transactions) -> {
            // 키에서 가맹점 ID와 연월 추출
            String[] parts = key.split("-");
            Long franchiseId = Long.parseLong(parts[0]);
            String yearMonth = parts[1] + "-" + parts[2];

            // 가맹점 이름 가져오기
            String franchiseName = transactions.get(0).getFranchise().getName();

            // 거래 건수
            int totalCount = transactions.size();

            // 정산 총액 계산
            BigDecimal totalAmount = transactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 정산 기간 계산 (해당 월의 첫날부터 마지막 날까지)
            LocalDate firstDay = LocalDate.of(
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    1
            );
            LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());
            String settlementPeriod = firstDay.toString() + " ~ " + lastDay.toString();

            // TransactionDto를 활용하여 요약 정보 생성
            TransactionDto summaryDto = new TransactionDto();

            // franchiseDto 객체 생성하고 설정
            FranchiseDto franchiseDto = new FranchiseDto();
            franchiseDto.setId(franchiseId);
            franchiseDto.setName(franchiseName);

            // 설정된 franchiseDto 객체를 summaryDto에 설정
            summaryDto.setFranchiseDto(franchiseDto);

            // 다른 필드들 설정
            summaryDto.setAmount(totalAmount);
            summaryDto.setClosed(true); // 정산 완료된 내역임을 표시

            // 트랜잭션 시간을 해당 월의 마지막 날로 설정 (월별 요약이므로)
            summaryDto.setTransactionTime(lastDay.atTime(23, 59, 59));

            summaries.add(summaryDto);
        });

        // 정렬 - 트랜잭션 시간 (월) 기준 내림차순, 동일 월이면 가맹점 이름 기준 오름차순
        summaries.sort((a, b) -> {
            int timeCompare = b.getTransactionTime().compareTo(a.getTransactionTime());
            if (timeCompare != 0) {
                return timeCompare;
            }
            return a.getFranchiseDto().getName().compareTo(b.getFranchiseDto().getName());
        });

        return summaries;
    }

    @Override
    public List<TransactionDto> findAllTransaction() {
        List<Transaction> transactions = transactionRepository.findByIsClosed(false);

        return transactions.stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class))
                .collect(Collectors.toList());
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
