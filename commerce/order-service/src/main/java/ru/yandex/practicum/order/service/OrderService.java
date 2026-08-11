package ru.yandex.practicum.order.service;

import ru.yandex.practicum.order.dto.OrderDto;
import ru.yandex.practicum.order.entity.Order;

import java.util.List;

public interface OrderService {
    OrderDto createConfirmed(Order order);

    OrderDto createPending(Order order);

    OrderDto getById(Long orderId);

    List<OrderDto> getOrders();

    List<OrderDto> getByEmail(String email);
}
