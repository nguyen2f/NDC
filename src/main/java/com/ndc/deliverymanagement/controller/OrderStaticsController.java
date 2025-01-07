package com.ndc.deliverymanagement.controller;

import com.ndc.deliverymanagement.dto.OrderHistoryDTO;
import com.ndc.deliverymanagement.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderStaticsController {
    private final OrderService orderService;

    public OrderStaticsController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order-statistics")
    public ResponseEntity<Map<String, Long>> getOrderStatistics() {
        Map<String, Long> statistics = orderService.getOrderStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/{orderId}/history")
    public ResponseEntity<List<OrderHistoryDTO>> getOrderHistory(@PathVariable Long orderId, HttpSession session) {
        List<OrderHistoryDTO> history = orderService.getOrderHistory(orderId);
        return ResponseEntity.ok(history);
    }

}