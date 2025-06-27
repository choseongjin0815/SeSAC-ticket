package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.SettlmentSummaryDto;
import com.onspring.onspring_customer.domain.common.dto.TransactionDto;
import com.onspring.onspring_customer.domain.common.entity.QTransaction;
import com.onspring.onspring_customer.domain.common.entity.Transaction;
import com.onspring.onspring_customer.domain.common.repository.TransactionRepository;
import com.onspring.onspring_customer.domain.customer.dto.PartyDto;
import com.onspring.onspring_customer.domain.customer.entity.Party;
import com.onspring.onspring_customer.domain.customer.entity.QAdmin;
import com.onspring.onspring_customer.domain.customer.entity.QCustomer;
import com.onspring.onspring_customer.domain.customer.entity.QParty;
import com.onspring.onspring_customer.domain.customer.repository.PartyRepository;
import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.entity.QFranchise;
import com.onspring.onspring_customer.domain.franchise.repository.FranchiseRepository;
import com.onspring.onspring_customer.domain.user.dto.EndUserDto;
import com.onspring.onspring_customer.domain.user.entity.EndUser;
import com.onspring.onspring_customer.domain.user.entity.QEndUser;
import com.onspring.onspring_customer.domain.user.repository.EndUserRepository;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final FranchiseRepository franchiseRepository;
    private final EndUserRepository endUserRepository;
    private final PartyRepository partyRepository;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;


    // 선택된 미정산 거래 내역(단 한개)가 정산으로 저장됨 isClosed = False -> True => Figma 홈_정산관리_정산확인
    @Override
    public Long saveFalseTransaction(TransactionDto transactionDto) {
        log.info("Saving transaction with ID: {}", transactionDto.getId());

        Transaction existingTransaction = transactionRepository.findById(transactionDto.getId())
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionDto.getId()));

        // 이미 정산 처리된 거래인지 확인
        if (existingTransaction.isClosed()) {
            throw new RuntimeException("Transaction is already closed with ID: " + transactionDto.getId());
        }

        // 정산 처리로 상태 변경 (isClosed = true)
        existingTransaction.closeTransaction();

        // 변경된 내용 저장
        Transaction savedTransaction = transactionRepository.save(existingTransaction);
        log.info("Transaction successfully closed: {}", savedTransaction.getId());

        return savedTransaction.getId();

    }

    // 선택된 미정산 거래 내역(여러 개)가 정산으로 저장됨 isClosed = False -> True => Figma 홈_정산관리_정산확인
    @Override
    @Transactional
    public List<Long> saveFalseTransactions(List<Long> transactionIds) {
        log.info("Processing multiple transactions: {}", transactionIds);

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
                transaction.closeTransaction();


                // 변경된 내용 저장
                Transaction savedTransaction = transactionRepository.save(transaction);
                log.info("Transaction successfully closed: {}", savedTransaction.getId());

                processedIds.add(savedTransaction.getId());
            } catch (Exception e) {
                log.error("Error processing transaction with ID {}: {}", id, e.getMessage());
                // 개별 트랜잭션 처리 실패 시에도 나머지 작업은 계속 진행
            }
        }

        log.info("Successfully closed {} transactions", processedIds.size());
        return processedIds;
    }

    @Override
    public Long saveTransaction(TransactionDto transactionDto) {
        return 0L;
    }

    @Override
    public TransactionDto findTransactionById(Long id) {
        return null;
    }

    /**
     * 사용자의 결제
     * @param transactionDto    결제 정보를 담은 transactionDto
     * @return transaction id
     */
    @Override
    public Long saveTransaction(Long partyId, TransactionDto transactionDto) {
        log.info("Saving transaction: {}", transactionDto);

        // FranchiseId와 UserId를 통해 가맹점과 사용자 정보를 조회
        Franchise franchise = franchiseRepository.findById(transactionDto.getFranchiseDto().getId())
                .orElseThrow(() -> new RuntimeException("Franchise not found"));
        log.info("franchise: " + franchise);

        EndUser endUser = endUserRepository.findById(transactionDto.getEndUserDto().getId())
                .orElseThrow(() -> new RuntimeException("EndUser not found"));
        log.info("endUser: " + endUser);

        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new RuntimeException("Party not found"));

        // TransactionDto -> Transaction 엔티티로 변환
        Transaction transaction = new Transaction(
                franchise,
                endUser,
                transactionDto.getAmount(),
                transactionDto.isClosed(),
                party
        );
//        transaction.setFranchise(franchise);
//        transaction.setEndUser(endUser);
//        transaction.setTransactionTime(LocalDateTime.now());
//        transaction.setAmount(transactionDto.getAmount());
//
//        transaction.setClosed(transactionDto.isClosed());
//
//        transaction.setParty(party);

        log.info("Saving transaction: {}", transaction);

        // 트랜잭션 저장
        Transaction savedTransaction = transactionRepository.save(transaction);

        // 저장된 트랜잭션 ID 반환
        return savedTransaction.getId();
    }




    // 정산 처리가 완료된(isClosed=true) 내역을 가맹점 기준으로 한달 간격으로 정리해서 요약 표시 => Figma
    @Override
    public List<TransactionDto> findMonthlySettlementSummary() {
        log.info("Finding monthly settlement summary by franchise");

        QTransaction transaction = QTransaction.transaction;
        QFranchise franchise = QFranchise.franchise;

        // fetchJoin으로 N+1 문제 해결
        List<Transaction> closedTransactions = queryFactory.selectFrom(transaction)
                .join(transaction.franchise, franchise).fetchJoin()
                .where(transaction.isClosed.eq(true))
                .fetch();

        if (closedTransactions.isEmpty()) {
            log.info("No closed transactions found");
            return new ArrayList<>();
        }

        Map<String, List<Transaction>> groupedTransactions = closedTransactions.stream()
                .collect(Collectors.groupingBy(t -> {
                    LocalDateTime date = t.getTransactionTime();
                    String yearMonth = date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                    return t.getFranchise().getId() + "-" + yearMonth;
                }));

        List<TransactionDto> summaries = new ArrayList<>();

        groupedTransactions.forEach((key, transactions) -> {
            String[] parts = key.split("-");
            Long franchiseId = Long.parseLong(parts[0]);
            String yearMonth = parts[1] + "-" + parts[2];

            String franchiseName = transactions.get(0).getFranchise().getName();
            int totalCount = transactions.size();
            BigDecimal totalAmount = transactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            LocalDate firstDay = LocalDate.of(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), 1);
            LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

            TransactionDto summaryDto = new TransactionDto();
            FranchiseDto franchiseDto = new FranchiseDto();
            franchiseDto.setId(franchiseId);
            franchiseDto.setName(franchiseName);

            summaryDto.setFranchiseDto(franchiseDto);
            summaryDto.setAmount(totalAmount);
            summaryDto.setClosed(true);
            summaryDto.setTransactionTime(lastDay.atTime(23, 59, 59));

            summaries.add(summaryDto);
        });

        summaries.sort((a, b) -> {
            int timeCompare = b.getTransactionTime().compareTo(a.getTransactionTime());
            if (timeCompare != 0) return timeCompare;
            return a.getFranchiseDto().getName().compareTo(b.getFranchiseDto().getName());
        });

        return summaries;
    }
    // 거래내역 중 미정산된 (isClosed = False) 모든 거래 내역 띄우기 => Figma 홈_정산관리_정산
    @Override
    public List<TransactionDto> findAllTransaction() {
        QTransaction transaction = QTransaction.transaction;
        QFranchise franchise = QFranchise.franchise;
        QParty party = QParty.party;
        QEndUser endUser = QEndUser.endUser;

        List<Transaction> transactions = queryFactory.selectFrom(transaction)
                .join(transaction.franchise, franchise).fetchJoin()
                .join(transaction.party, party).fetchJoin()
                .join(transaction.endUser, endUser).fetchJoin()
                .where(transaction.isClosed.eq(false))
                .fetch();

        if (transactions.isEmpty()) {
            log.info("No open transactions found");
            return new ArrayList<>();
        }

        return transactions.stream()
                .map(tx -> modelMapper.map(tx, TransactionDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<TransactionDto> findAllAcceptedAndNotClosedTransaction(Long adminId, Pageable pageable) {
        QTransaction tx = QTransaction.transaction;
        QFranchise franchise = QFranchise.franchise;
        QParty party = QParty.party;
        QCustomer customer = QCustomer.customer;
        QAdmin admin = QAdmin.admin;
        QEndUser endUser = QEndUser.endUser;

        // content 쿼리
        List<Transaction> content = queryFactory.selectFrom(tx)
                .join(tx.party, party).fetchJoin()
                .join(party.customer, customer)
                .join(customer.admins, admin)
                .join(tx.franchise, franchise).fetchJoin()
                .join(tx.endUser, endUser).fetchJoin()
                .where(
                        admin.id.eq(adminId),
                        tx.isAccepted.isTrue(),
                        tx.isClosed.isFalse()
                )
                .orderBy(tx.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리 (fetchJoin X)
        Long total = queryFactory.select(tx.count())
                .from(tx)
                .join(tx.party, party)
                .join(party.customer, customer)
                .join(customer.admins, admin)
                .where(
                        admin.id.eq(adminId),
                        tx.isAccepted.isTrue(),
                        tx.isClosed.isFalse()
                )
                .fetchOne();

        return new PageImpl<>(content.stream()
                .map(t -> modelMapper.map(t, TransactionDto.class))
                .toList(), pageable, total);
    }

    @Override
    public Page<TransactionDto> findAllTransactionByQuery(Long adminId, String by, String name, LocalDate after,
                                                          LocalDate before,
                                                          Pageable pageable) {
        QAdmin admin = QAdmin.admin;
        QCustomer customer = QCustomer.customer;
        QParty party = QParty.party;
        QTransaction transaction = QTransaction.transaction;
        JPAQuery<Transaction> query = queryFactory.selectFrom(transaction)
                .join(transaction.party, party)
                .join(party.customer, customer)
                .join(customer.admins, admin)
                .where(admin.id.eq(adminId));

        if (name != null) {
            query.where(switch (by) {
                case "franchise" -> transaction.franchise.name.containsIgnoreCase(name);
                case "party" -> transaction.party.name.containsIgnoreCase(name);
                case "user" -> transaction.endUser.name.containsIgnoreCase(name);
                default -> throw new IllegalStateException("Unexpected value: " + by);
            });
        }
        if (after != null) {
            query.where(transaction.transactionTime.goe(after.atStartOfDay()));
        }
        if (before != null) {
            query.where(transaction.transactionTime.loe(before.atTime(LocalTime.MAX)));
        }

        Long count = Objects.requireNonNull(query.clone()
                .select(transaction.count())
                .fetchOne());

        query.orderBy(transaction.id.desc());

        query.offset(pageable.getOffset());
        query.limit(pageable.getPageSize());

        List<Transaction> transactions = query.fetch();

        List<TransactionDto> transactionDtoList = transactions.stream()
                .map(element -> {
                    TransactionDto transactionDto = modelMapper.map(element, TransactionDto.class);
                    transactionDto.setFranchiseDto(modelMapper.map(element.getFranchise(), FranchiseDto.class));
                    transactionDto.setPartyDto(modelMapper.map(element.getParty(), PartyDto.class));
                    transactionDto.setEndUserDto(modelMapper.map(element.getEndUser(), EndUserDto.class));

                    return transactionDto;
                })
                .toList();

        return new PageImpl<>(transactionDtoList, pageable, count);
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
    public List<TransactionDto> findTransactionByFranchiseId(Long franchiseId, LocalDateTime startDate, LocalDateTime endDate, String period) {
        // 제공된 경우 period를 사용하여 날짜 범위 결정
        if (period != null) {
            switch (period) {
                case "오늘":
                    startDate = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
                    endDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
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
                    throw new IllegalArgumentException("유효하지 않은 period 값: " + period);
            }
        }

        List<Transaction> transactions = null;

        // 시작 및 종료 날짜가 모두 있는지 확인(period에서 또는 매개변수에서)
        if (startDate != null && endDate != null) {
            log.info("날짜 범위로 트랜잭션 찾기 - startDate: " + startDate + ", endDate: " + endDate);
            transactions = transactionRepository.findTransactionsByFranchiseIdAndDateRange(franchiseId, startDate, endDate);

        }

        // 결과 변환 및 반환
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
    public Page<TransactionDto> findTransactionByEndUserId(Long userId, Pageable pageable) {
        return transactionRepository.findByEndUserId(userId, pageable)
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class));

    }

    /**
     * 특정 가맹점의 해당 월에 대한 정산내역 조회 (이후 페이징등 추가 고려 필요)
     *
     * @param franchiseId     조회할 가맹점의 id
     * @param month           정산 월
     * @param period          오늘, 최근1주, 최근2주, 최근3주 등
     * @param startDate 조회 시작일
     * @param endDate   조회 종료일

     * @return  해당 가맹점의 기간 필터링을 적용한 TransactionDto 반환
     */
    @Override
    public List<TransactionDto> findSettlementByFranchiseId(Long franchiseId, String month, String period, LocalDateTime startDate, LocalDateTime endDate) {

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
        transaction.cancelTransaction();
        transactionRepository.save(transaction);

        return true;
    }
}
