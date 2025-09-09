package com.sba301.order_service.client;

import com.sba301.order_service.dto.response.TicketResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "event-service")
public interface EventServiceClient {
    @GetMapping("/events/tickets/{id}")
    ResponseEntity<TicketResponse> getEventTicketById(@PathVariable("id") Integer id);

    @PostMapping("/events/tickets/{id}/quantity/{quantity}")
    ResponseEntity<TicketResponse> updateEventTicketQuantity(@PathVariable("id") Integer id, @PathVariable("quantity") Integer quantity);
}

