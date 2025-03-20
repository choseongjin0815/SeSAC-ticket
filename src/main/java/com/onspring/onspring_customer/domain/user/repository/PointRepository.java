package com.onspring.onspring_customer.domain.user.repository;

import com.onspring.onspring_customer.domain.user.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {


    List<Point> findByEndUserId(Long endUserId);
}