package com.onspring.onspring_customer.domain.common.service;

import com.onspring.onspring_customer.domain.common.dto.TransactionArchiveDto;
import com.onspring.onspring_customer.domain.common.entity.QTransaction;
import com.onspring.onspring_customer.domain.common.entity.TransactionArchive;
import com.onspring.onspring_customer.domain.common.repository.TransactionArchiveRepository;
import com.onspring.onspring_customer.domain.common.repository.TransactionRepository;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import com.onspring.onspring_customer.domain.franchise.entity.QFranchise;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
@Service
public class TransactionArchiveServiceImpl implements TransactionArchiveService {
    private final ModelMapper modelMapper;
    private final TransactionRepository transactionRepository;
    private final TransactionArchiveRepository transactionArchiveRepository;
    private final JPAQueryFactory queryFactory;

    @Autowired
    public TransactionArchiveServiceImpl(TransactionRepository transactionRepository,
                                         TransactionArchiveRepository transactionArchiveRepository,
                                         JPAQueryFactory queryFactory, ModelMapper modelMapper) {
        this.transactionRepository = transactionRepository;
        this.transactionArchiveRepository = transactionArchiveRepository;
        this.queryFactory = queryFactory;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<Long> closeTransactionById(List<Long> ids) {
        log.info("Closing transaction with ID {}", ids.toString());

        List<Long> changedTransactionIds = ids.stream()
                .filter(id -> transactionRepository.updateIsClosedByIdAndIsAcceptedTrueAndIsClosedFalse(id) == 1)
                .toList();

        log.info("{} records changed, {} records remain unchanged", changedTransactionIds.size(),
                ids.size() - changedTransactionIds.size());

        QTransaction transaction = QTransaction.transaction;
        QFranchise franchise = QFranchise.franchise;

        AtomicLong updateCount = new AtomicLong();
        AtomicLong saveCount = new AtomicLong();

        queryFactory.select(franchise, transaction.count(), transaction.amount.sumAggregate(),
                        transaction.transactionTime.year(), transaction.transactionTime.month())
                .from(transaction)
                .join(franchise)
                .on(transaction.franchise.id.eq(franchise.id))
                .groupBy(franchise.name, transaction.transactionTime.year(), transaction.transactionTime.month())
                .fetch()
                .forEach(fetchedRecord -> {
                    Franchise archiveFranchise = Objects.requireNonNull(fetchedRecord.get(franchise));
                    Long transactionCount = Objects.requireNonNull(fetchedRecord.get(transaction.count()));
                    BigDecimal amountSum = Objects.requireNonNull(fetchedRecord.get(transaction.amount.sumAggregate()));
                    int year = Objects.requireNonNull(fetchedRecord.get(transaction.transactionTime.year()));
                    int month = Objects.requireNonNull(fetchedRecord.get(transaction.transactionTime.month()));
                    LocalDate duration = LocalDate.of(year, month, 1);

                    Optional<TransactionArchive> existingTransactionArchive =
                            transactionArchiveRepository.findByFranchiseAndDuration(archiveFranchise, duration);
                    if (existingTransactionArchive.isPresent()) {
                        existingTransactionArchive.get()
                                .setTransactionCount(existingTransactionArchive.get()
                                                             .getTransactionCount() + transactionCount);
                        existingTransactionArchive.get()
                                .setAmountSum(existingTransactionArchive.get()
                                        .getAmountSum()
                                        .add(amountSum));
                        transactionArchiveRepository.save(existingTransactionArchive.get());

                        updateCount.getAndIncrement();
                    } else {
                        TransactionArchive archive = new TransactionArchive();
                        archive.setFranchise(archiveFranchise);
                        archive.setTransactionCount(transactionCount);
                        archive.setAmountSum(amountSum);
                        archive.setDuration(duration);

                        transactionArchiveRepository.save(archive);

                        saveCount.getAndIncrement();
                    }
                });

        log.info("{} archived data updated, {} new archive data saved", updateCount.get(), saveCount.get());

        return Arrays.asList(updateCount.get(), saveCount.get());
    }

    @Override
    public Page<TransactionArchiveDto> findAllTransactionArchive(Pageable pageable) {
        return transactionArchiveRepository.findAll(pageable)
                .map(element -> modelMapper.map(element, TransactionArchiveDto.class));
    }
}
