package com.onspring.onspring_customer.domain.user.repository;

import com.onspring.onspring_customer.domain.user.entity.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {

    List<Point> findByEndUserId(Long endUserId);

    Page<Point> findByEndUser_Id(@NonNull Long id, Pageable pageable);

}