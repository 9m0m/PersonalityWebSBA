package com.sba301.order_service.controller;

import com.sba301.order_service.dto.request.CreateOrderRequest;
import com.sba301.order_service.dto.response.OrderItemResponse;
import com.sba301.order_service.dto.response.OrderResponse;
import com.sba301.order_service.entity.Order;
import com.sba301.order_service.entity.enums.ItemType;
import com.sba301.order_service.service.OrderService;
import com.sba301.order_service.util.HeaderExtractor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest request, HttpServletRequest httpRequest) {
        String userId = HeaderExtractor.getUserId(httpRequest);
        Order order = orderService.createOrder(request, userId);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer id) {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }


    @GetMapping("/me")
    public ResponseEntity<Page<OrderResponse>> getOrderPaginationByUserIdWithFilterByOrderStatus(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest
    ) {
        String userId = HeaderExtractor.getUserId(httpRequest);
        Page<OrderResponse> orders = orderService.getOrderPaginationByUserIdWithStatus(userId, status, page, size);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/me/items")
    public ResponseEntity<Page<OrderItemResponse>> getItemPaginationByUserIdWithFilter(
            @RequestParam(required = false) ItemType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest
    ) {
        String userId = HeaderExtractor.getUserId(httpRequest);
        Page<OrderItemResponse> orders = orderService.getItemPaginationByUserIdWithFilter(userId, type.toString(), page, size);
        return ResponseEntity.ok(orders);
    }



    @PostMapping("/{orderId}/complete")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable Integer orderId) {
        OrderResponse orderResponse = orderService.completeOrder(orderId);
        return ResponseEntity.ok(orderResponse);
    }

    @PostMapping("/{orderId}/abandon")
    public ResponseEntity<OrderResponse> abandonOrder(@PathVariable Integer orderId) {
        System.out.println("Abandoning order with ID: " + orderId);
        OrderResponse orderResponse = orderService.abandonOrder(orderId);
        return ResponseEntity.ok(orderResponse);
    }
}
