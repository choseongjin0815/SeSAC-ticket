package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.TransactionArchiveDto;
import com.onspring.onspring_customer.domain.common.entity.QTransaction;
import com.onspring.onspring_customer.domain.common.entity.TransactionArchive;
import com.onspring.onspring_customer.domain.common.repository.TransactionArchiveRepository;
import com.onspring.onspring_customer.domain.common.repository.TransactionRepository;
import com.onspring.onspring_customer.domain.customer.entity.Customer;
import com.onspring.onspring_customer.domain.customer.repository.AdminRepository;
import com.onspring.onspring_customer.domain.franchise.dto.FranchiseDto;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.entity.QFranchise;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@RequiredArgsConstructor
@Service
public class TransactionArchiveServiceImpl implements TransactionArchiveService {
    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final TransactionRepository transactionRepository;
    private final TransactionArchiveRepository transactionArchiveRepository;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Integer> closeTransactionById(Long adminId, List<Long> ids) {
        log.info("Closing transaction with ID {}", ids.toString());

        int updatedTransactionsCount = transactionRepository.updateIsClosedByIdInAndIsAcceptedTrueAndIsClosedFalse(ids);

        log.info("{} records changed, {} records remain unchanged", updatedTransactionsCount,
                ids.size() - updatedTransactionsCount);

        Customer customer = adminRepository.findById(adminId)
                .orElseThrow()
                .getCustomer();

        QTransaction transaction = QTransaction.transaction;
        QFranchise franchise = QFranchise.franchise;

        List<Tuple> aggregatedData = queryFactory.select(franchise, transaction.count(),
                        transaction.amount.sumAggregate(), transaction.transactionTime.year(),
                        transaction.transactionTime.month())
                .from(transaction)
                .join(transaction.franchise, franchise)
                .where(transaction.id.in(ids))
                .groupBy(franchise, transaction.transactionTime.year(), transaction.transactionTime.month())
                .fetch();

        Set<LocalDate> durations = aggregatedData.stream()
                .map(tuple -> LocalDate.of(tuple.get(transaction.transactionTime.year()),
                        tuple.get(transaction.transactionTime.month()), 1))
                .collect(Collectors.toSet());

        Map<String, TransactionArchive> existingArchives = transactionArchiveRepository.findByDurationIn(durations)
                .stream()
                .collect(Collectors.toMap(transactionArchive -> transactionArchive.getFranchise()
                                                                        .getName() + "_" + transactionArchive.getDuration(), Function.identity()));

        List<TransactionArchive> archivesToUpdate = new ArrayList<>();
        List<TransactionArchive> archivesToSave = new ArrayList<>();

        aggregatedData.forEach(tuple -> {
            Franchise franchise1 = tuple.get(franchise);
            Long count = tuple.get(transaction.count());
            BigDecimal amountSum = tuple.get(transaction.amount.sumAggregate());
            LocalDate duration = LocalDate.of(tuple.get(transaction.transactionTime.year()),
                    tuple.get(transaction.transactionTime.month()), 1);

            String key = Objects.requireNonNull(franchise1)
                                 .getName() + "_" + duration;
            TransactionArchive transactionArchive = existingArchives.get(key);

            if (transactionArchive != null) {
                transactionArchive.setTransactionCount(transactionArchive.getTransactionCount() + count);
                transactionArchive.setAmountSum(transactionArchive.getAmountSum()
                        .add(amountSum));
                archivesToUpdate.add(transactionArchive);
            } else {
                TransactionArchive archiveToSave = TransactionArchive.builder()
                        .customer(customer)
                        .franchise(franchise1)
                        .transactionCount(count)
                        .amountSum(amountSum)
                        .duration(duration)
                        .build();

                archivesToSave.add(archiveToSave);
            }
        });

        transactionArchiveRepository.saveAll(Stream.concat(archivesToUpdate.stream(), archivesToSave.stream())
                .toList());

        log.info("{} archived data updated, {} new archive data saved", archivesToUpdate.size(), archivesToSave.size());

        return Arrays.asList(archivesToUpdate.size(), archivesToSave.size());
    }

    @Override
    public Page<TransactionArchiveDto> findAllTransactionArchive(Long adminId, Pageable pageable) {
        List<TransactionArchiveDto> transactionArchiveDtoList = transactionArchiveRepository.findAllByOrderByIdDesc()
                .stream()
                .filter(transactionArchive -> transactionArchive.getCustomer()
                        .getAdmins()
                        .stream()
                        .anyMatch(admin -> admin.getId().equals(adminId)))
                .map(element -> {
                    TransactionArchiveDto transactionArchiveDto = modelMapper.map(element, TransactionArchiveDto.class);
                    transactionArchiveDto.setFranchiseDto(modelMapper.map(element.getFranchise(), FranchiseDto.class));
                    return transactionArchiveDto;
                })
                .toList();

        int start = (int) pageable.getOffset(); // 시작 인덱스
        int end = Math.min((start + pageable.getPageSize()), transactionArchiveDtoList.size()); // 끝 인덱스
        List<TransactionArchiveDto> pagedList = transactionArchiveDtoList.subList(start, end);

        return new PageImpl<>(pagedList, pageable, transactionArchiveDtoList.size());
    }
}
