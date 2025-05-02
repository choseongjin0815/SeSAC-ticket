package com.onspring.onspring_customer.domain.user.repository;

import com.onspring.onspring_customer.domain.user.entity.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByParty_Customer_Admins_Id(@NonNull Long id);

    List<Point> findByParty_Id(@NonNull Long id);

    Page<Point> findByParty_IdNot(@NonNull Long id, Pageable pageable);

    List<Point> findByParty_IdAndEndUser_IdIn(@NonNull Long id, @NonNull Collection<Long> ids);

    List<Point> findByEndUserId(Long endUserId);

    Page<Point> findByEndUser_Id(@NonNull Long id, Pageable pageable);

    Optional<Point> findByParty_IdAndEndUser_Id(@NonNull Long partyId, @NonNull Long endUserId);

    long deleteByParty_IdAndEndUser_IdIn(@NonNull Long id, @NonNull Collection<Long> ids);

}