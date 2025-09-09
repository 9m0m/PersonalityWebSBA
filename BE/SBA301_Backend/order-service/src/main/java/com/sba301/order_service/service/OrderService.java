package com.sba301.order_service.service;

import com.sba301.order_service.dto.request.CreateOrderRequest;
import com.sba301.order_service.dto.response.OrderItemResponse;
import com.sba301.order_service.dto.response.OrderResponse;
import com.sba301.order_service.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Order createOrder(CreateOrderRequest request, String userId);

    OrderResponse getOrderById(Integer id);

    Page<OrderResponse> getOrderPaginationByUserIdWithStatus(String userId, String status, int page, int size);

    OrderResponse completeOrder(Integer orderId);

    OrderResponse abandonOrder(Integer orderId);

    Page<OrderItemResponse> getItemPaginationByUserIdWithFilter(String userId, String type, int page, int size);

}
