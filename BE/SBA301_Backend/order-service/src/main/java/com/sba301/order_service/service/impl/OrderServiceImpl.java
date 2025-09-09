package com.sba301.order_service.service.impl;

import com.sba301.order_service.client.EventServiceClient;
import com.sba301.order_service.client.PremiumServiceClient;
import com.sba301.order_service.dto.request.CreateOrderRequest;
import com.sba301.order_service.dto.request.OrderItemRequest;
import com.sba301.order_service.dto.request.SubcriptionRequest;
import com.sba301.order_service.dto.response.OrderItemResponse;
import com.sba301.order_service.dto.response.OrderResponse;
import com.sba301.order_service.dto.response.PremiumResponse;
import com.sba301.order_service.dto.response.TicketResponse;
import com.sba301.order_service.entity.Order;
import com.sba301.order_service.entity.OrderItem;
import com.sba301.order_service.entity.enums.ItemType;
import com.sba301.order_service.entity.enums.OrderStatus;
import com.sba301.order_service.exception.AppException;
import com.sba301.order_service.repository.OrderItemRepository;
import com.sba301.order_service.repository.OrderRepository;
import com.sba301.order_service.service.OrderService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PremiumServiceClient premiumServiceClient;
    private final EventServiceClient eventServiceClient;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                            PremiumServiceClient premiumServiceClient, EventServiceClient eventServiceClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.premiumServiceClient = premiumServiceClient;
        this.eventServiceClient = eventServiceClient;
    }

    @Override
    @Transactional
    public Order createOrder(CreateOrderRequest request, String userId) {
        try {
            // Validate each item by calling the appropriate service
            Double price = 0.0;

            for (OrderItemRequest itemReq : request.getItems()) {
                if (itemReq.getItemType().toString().equals(ItemType.PREMIUM.toString())) {
                    ResponseEntity<PremiumResponse> ex = premiumServiceClient.getPremiumById(itemReq.getItemId());
                    PremiumResponse premium = ex.getBody();
                    price += premium.price() * itemReq.getQuantity();
                } else if (itemReq.getItemType().toString().equals(ItemType.TICKET.toString())) {
                    ResponseEntity<TicketResponse> ex = eventServiceClient.getEventTicketById(itemReq.getItemId());
                    TicketResponse ticket = ex.getBody();
                    //check quantity validity
                    String alertMessage = "Not enough tickets available for itemId: " + itemReq.getItemId() + ", remaining: " + (ticket != null ? ticket.quantity() : 0);
                    if (ticket.quantity() < itemReq.getQuantity()) {
                        throw new AppException(400, alertMessage, HttpStatus.BAD_REQUEST);
                    }
                    price += ticket.price() * itemReq.getQuantity();
                } else {
                    throw new AppException(400, "Unsupported item type: " + itemReq.getItemType(), HttpStatus.BAD_REQUEST);
                }
            }

            // Create order with empty items list
            Order order = Order.builder()
                    .time(LocalDateTime.now())
                    .status(OrderStatus.PENDING)
                    .total(price)
                    .userId(userId)
                    .items(List.of())
                    .build();

            final Order savedOrder = orderRepository.save(order);
            List<OrderItem> items = request.getItems().stream()
                    .map(itemReq -> OrderItem.builder()
                            .itemId(itemReq.getItemId())
                            .itemType(itemReq.getItemType())
                            .quantity(itemReq.getQuantity())
                            .order(savedOrder)
                            .build())
                    .collect(Collectors.toList());

            // Save items and set them to order
            items = orderItemRepository.saveAll(items);
            savedOrder.setItems(items);
            return savedOrder;
        } catch (FeignException e) {
            log.debug(e.getMessage());
            throw e;
        } catch (AppException e) {
            log.debug(e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new AppException(500, "Failed to create order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public OrderResponse getOrderById(Integer id) {
        try {
            Order o = orderRepository.findById(id)
                    .orElseThrow(() -> new AppException(404, "Order not found with id: " + id, HttpStatus.NOT_FOUND));

            List<OrderItemResponse> oRes = o.getItems().stream().map(i -> {
                if (i.getItemType().toString().equals(ItemType.PREMIUM.toString())) {
                    ResponseEntity<PremiumResponse> ex = premiumServiceClient.getPremiumById(i.getItemId());
                    PremiumResponse premium = ex.getBody();
                    return OrderItemResponse.builder()
                            .id(i.getId())
                            .itemId(i.getItemId())
                            .quantity(i.getQuantity())
                            .itemType(i.getItemType().name())
                            .itemName(premium.name())
                            .itemPrice((double) premium.price())
                            .build();
                } else if (i.getItemType().toString().equals(ItemType.TICKET.toString())) {
                    ResponseEntity<TicketResponse> ex = eventServiceClient.getEventTicketById(i.getItemId());
                    TicketResponse ticket = ex.getBody();
                    return OrderItemResponse.builder()
                            .id(i.getId())
                            .itemId(i.getItemId())
                            .quantity(i.getQuantity())
                            .itemType(i.getItemType().name())
                            .itemName(ticket != null ? ticket.name() : null)
                            .itemPrice(ticket != null ? ticket.price() : null)
                            .build();
                } else {
                    throw new AppException(400, "Unsupported item type: " + i.getItemType(), HttpStatus.BAD_REQUEST);
                }
            }).collect(Collectors.toList());

            return OrderResponse.builder()
                    .id(o.getId())
                    .time(o.getTime())
                    .total(o.getTotal())
                    .status(o.getStatus().name())
                    .userId(o.getUserId())
                    .items(oRes)
                    .build();
        } catch (FeignException e) {
            log.info(e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new AppException(500, "Failed to create order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<OrderResponse> getOrderPaginationByUserIdWithStatus(String userId, String status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orderPage;
            if (status == null || status.isEmpty()) {
                orderPage = orderRepository.findByUserIdOrderByTimeDesc(userId, pageable);
            } else {
                OrderStatus orderStatus;
                try {
                    orderStatus = OrderStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new AppException(400, "Invalid order status: " + status, HttpStatus.BAD_REQUEST);
                }
                orderPage = orderRepository.findByUserIdAndStatusOrderByTimeDesc(userId, orderStatus, pageable);
            }
            // Map each Order to OrderResponse with item details
            Page<OrderResponse> responsePage = orderPage.map(order -> {
                List<OrderItemResponse> itemResponses = order.getItems().stream().map(i -> {
                    if (i.getItemType().toString().equals(ItemType.PREMIUM.toString())) {
                        ResponseEntity<PremiumResponse> ex = premiumServiceClient.getPremiumById(i.getItemId());
                        PremiumResponse premium = ex.getBody();
                        return OrderItemResponse.builder()
                                .id(i.getId())
                                .itemId(i.getItemId())
                                .quantity(i.getQuantity())
                                .itemType(i.getItemType().name())
                                .itemName(premium != null ? premium.name() : null)
                                .itemPrice(premium != null ? (double) premium.price() : null)
                                .build();
                    } else if (i.getItemType().toString().equals(ItemType.TICKET.toString())) {
                        ResponseEntity<TicketResponse> ex = eventServiceClient.getEventTicketById(i.getItemId());
                        TicketResponse ticket = ex.getBody();
                        return OrderItemResponse.builder()
                                .id(i.getId())
                                .itemId(i.getItemId())
                                .quantity(i.getQuantity())
                                .itemType(i.getItemType().name())
                                .itemName(ticket != null ? ticket.name() : null)
                                .itemPrice(ticket != null ? ticket.price() : null)
                                .build();
                    } else {
                        throw new AppException(400, "Unsupported item type: " + i.getItemType(), HttpStatus.BAD_REQUEST);
                    }
                }).collect(Collectors.toList());
                return OrderResponse.builder()
                        .id(order.getId())
                        .time(order.getTime())
                        .total(order.getTotal())
                        .status(order.getStatus().name())
                        .userId(order.getUserId())
                        .items(itemResponses)
                        .build();
            });
            return responsePage;
        } catch (FeignException e) {
            log.info(e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new AppException(500, "Failed to create order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public OrderResponse completeOrder(Integer orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(404, "Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

            if (!order.getStatus().toString().equals(OrderStatus.PENDING.toString())) {
                throw new AppException(400, "Order is not in PENDING status: " + order.getStatus(), HttpStatus.BAD_REQUEST);
            }

            // Update order status to COMPLETED
            order.setStatus(OrderStatus.COMPLETED);

            // Map to response
            List<OrderItemResponse> itemResponses = order.getItems().stream().map(i -> {
                if (i.getItemType().toString().equals(ItemType.PREMIUM.toString())) {
                    ResponseEntity<PremiumResponse> ex = premiumServiceClient.getPremiumById(i.getItemId());
                    PremiumResponse premium = ex.getBody();

                    // create premium subscription
                    ResponseEntity<Integer> subscriptionResponse = premiumServiceClient.createSubscription(
                            new SubcriptionRequest(i.getItemId(), order.getUserId()));

                    return OrderItemResponse.builder()
                            .id(i.getId())
                            .itemId(i.getItemId())
                            .quantity(i.getQuantity())
                            .itemType(i.getItemType().name())
                            .itemName(premium != null ? premium.name() : null)
                            .itemPrice(premium != null ? (double) premium.price() : null)
                            .build();
                } else if (i.getItemType().toString().equals(ItemType.TICKET.toString())) {
                    ResponseEntity<TicketResponse> ex = eventServiceClient.getEventTicketById(i.getItemId());
                    TicketResponse ticket = ex.getBody();
                    // Update ticket quantity
                    TicketResponse updateTicketResponse = eventServiceClient.updateEventTicketQuantity(i.getItemId(), i.getQuantity()).getBody();
                    return OrderItemResponse.builder()
                            .id(i.getId())
                            .itemId(i.getItemId())
                            .quantity(updateTicketResponse.quantity())
                            .itemType(i.getItemType().name())
                            .itemName(ticket != null ? ticket.name() : null)
                            .itemPrice(ticket != null ? ticket.price() : null)
                            .build();
                } else {
                    throw new AppException(400, "Unsupported item type: " + i.getItemType(), HttpStatus.BAD_REQUEST);
                }
            }).collect(Collectors.toList());

            Order savedOrder = orderRepository.save(order);

            return OrderResponse.builder()
                    .id(savedOrder.getId())
                    .time(savedOrder.getTime())
                    .total(savedOrder.getTotal())
                    .status(savedOrder.getStatus().name())
                    .userId(savedOrder.getUserId())
                    .items(itemResponses)
                    .build();

        } catch (FeignException e) {
            log.info(e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new AppException(500, "Failed to complete order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public OrderResponse abandonOrder(Integer orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new AppException(404, "Order not found with id: " + orderId, HttpStatus.NOT_FOUND));

            if (order.getStatus() != OrderStatus.PENDING) {
                throw new AppException(400, "Order is not in PENDING status: " + order.getStatus(), HttpStatus.BAD_REQUEST);
            }

            // Update order status to ABANDONED
            order.setStatus(OrderStatus.CANCELLED);

            // Map to response
            List<OrderItemResponse> itemResponses = order.getItems().stream().map(i -> {
                if (i.getItemType().toString().equals(ItemType.PREMIUM.toString())) {
                    ResponseEntity<PremiumResponse> ex = premiumServiceClient.getPremiumById(i.getItemId());
                    PremiumResponse premium = ex.getBody();
                    return OrderItemResponse.builder()
                            .id(i.getId())
                            .itemId(i.getItemId())
                            .quantity(i.getQuantity())
                            .itemType(i.getItemType().name())
                            .itemName(premium != null ? premium.name() : null)
                            .itemPrice(premium != null ? (double) premium.price() : null)
                            .build();
                } else if (i.getItemType().toString().equals(ItemType.TICKET.toString())) {
                    ResponseEntity<TicketResponse> ex = eventServiceClient.getEventTicketById(i.getItemId());
                    TicketResponse ticket = ex.getBody();
                    return OrderItemResponse.builder()
                            .id(i.getId())
                            .itemId(i.getItemId())
                            .quantity(i.getQuantity())
                            .itemType(i.getItemType().name())
                            .itemName(ticket != null ? ticket.name() : null)
                            .itemPrice(ticket != null ? ticket.price() : null)
                            .build();
                } else {
                    throw new AppException(400, "Unsupported item type: " + i.getItemType(), HttpStatus.BAD_REQUEST);
                }
            }).collect(Collectors.toList());

            Order savedOrder = orderRepository.save(order);

            return OrderResponse.builder()
                    .id(savedOrder.getId())
                    .time(savedOrder.getTime())
                    .total(savedOrder.getTotal())
                    .status(savedOrder.getStatus().name())
                    .userId(savedOrder.getUserId())
                    .items(itemResponses)
                    .build();

        } catch (FeignException e) {
            log.info(e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new AppException(500, "Failed to abandon order: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<OrderItemResponse> getItemPaginationByUserIdWithFilter(String userId, String type, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            ItemType itemType = type != null ? ItemType.valueOf(type) : null;

            Page<OrderItem> orderItems = orderItemRepository.findByOrderUserIdAndItemTypeOrderByOrderTimeDesc(userId, itemType, pageable);

            return orderItems.map(item -> {
                if (item.getItemType() == ItemType.PREMIUM) {
                    ResponseEntity<PremiumResponse> ex = premiumServiceClient.getPremiumById(item.getItemId());
                    PremiumResponse premium = ex.getBody();
                    return OrderItemResponse.builder()
                            .id(item.getId())
                            .itemId(item.getItemId())
                            .quantity(item.getQuantity())
                            .itemType(item.getItemType().name())
                            .itemName(premium != null ? premium.name() : null)
                            .itemPrice(premium != null ? (double) premium.price() : null)
                            .build();
                } else if (item.getItemType() == ItemType.TICKET) {
                    ResponseEntity<TicketResponse> ex = eventServiceClient.getEventTicketById(item.getItemId());
                    TicketResponse ticket = ex.getBody();
                    return OrderItemResponse.builder()
                            .id(item.getId())
                            .itemId(item.getItemId())
                            .quantity(item.getQuantity())
                            .itemType(item.getItemType().name())
                            .itemName(ticket != null ? ticket.name() : null)
                            .itemPrice(ticket != null ? ticket.price() : null)
                            .build();
                } else {
                    throw new AppException(400, "Unsupported item type: " + item.getItemType(), HttpStatus.BAD_REQUEST);
                }
            });
        } catch (IllegalArgumentException e) {
            throw new AppException(400, "Invalid item type: " + type, HttpStatus.BAD_REQUEST);
        } catch (FeignException e) {
            log.info(e.getMessage());
            throw e;
        } catch (Exception e) {
            throw new AppException(500, "Failed to get items: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
