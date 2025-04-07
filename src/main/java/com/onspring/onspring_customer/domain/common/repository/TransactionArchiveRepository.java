package com.onspring.onspring_customer.domain.common.repository;

import com.onspring.onspring_customer.domain.common.entity.TransactionArchive;
import com.onspring.onspring_customer.domain.franchise.entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.Optional;

public interface TransactionArchiveRepository extends JpaRepository<TransactionArchive, Long> {
    Optional<TransactionArchive> findByFranchiseAndDuration(@NonNull Franchise franchise, @NonNull LocalDate duration);
}