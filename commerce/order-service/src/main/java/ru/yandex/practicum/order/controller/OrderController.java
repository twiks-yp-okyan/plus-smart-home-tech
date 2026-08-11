package ru.yandex.practicum.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.order.dto.CreateOrderRequest;
import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.service.OrderOrchestrationService;
import ru.yandex.practicum.order.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderOrchestrationService orderOrchestrationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto create(@Valid @RequestBody CreateOrderRequest request) {
        return orderOrchestrationService.createOrder(request);
    }

    @GetMapping
    public List<OrderDto> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/{id}")
    public OrderDto getById(@PathVariable long id) {
        return orderService.getById(id);
    }

    @GetMapping("/by-email")
    public List<OrderDto> getByCustomerEmail(@RequestParam(value = "email") String email) {
        return orderService.getByEmail(email);
    }
}
