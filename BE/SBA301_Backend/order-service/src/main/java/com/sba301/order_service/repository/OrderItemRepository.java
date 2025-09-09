package com.sba301.order_service.repository;

import com.sba301.order_service.entity.OrderItem;
import com.sba301.order_service.entity.enums.ItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.userId = :userId AND (:itemType IS NULL OR oi.itemType = :itemType) ORDER BY oi.order.time DESC")
    Page<OrderItem> findByOrderUserIdAndItemTypeOrderByOrderTimeDesc(String userId, ItemType itemType, Pageable pageable);
}
