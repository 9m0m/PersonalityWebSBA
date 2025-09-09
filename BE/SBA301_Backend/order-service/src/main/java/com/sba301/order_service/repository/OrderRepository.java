package com.sba301.order_service.repository;

import com.sba301.order_service.entity.Order;
import com.sba301.order_service.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Page<Order> findByUserIdOrderByTimeDesc(String userId, Pageable pageable);
    Page<Order> findByUserIdAndStatusOrderByTimeDesc(String userId, OrderStatus status, Pageable pageable);
}
