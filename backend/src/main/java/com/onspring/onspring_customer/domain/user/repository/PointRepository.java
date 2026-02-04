package com.onspring.onspring_customer.domain.user.repository;

import com.onspring.onspring_customer.domain.user.entity.Point;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    //포인트 결제 도중 관리자가 포인트 차감했을 때 처리
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Point p where p.id = :id")
    Optional<Point> findByIdForUpdate(@Param("id") Long id);

}