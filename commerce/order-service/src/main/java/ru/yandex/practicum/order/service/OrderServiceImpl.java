package ru.yandex.practicum.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.order.dto.*;
import ru.yandex.practicum.order.entity.Order;
import ru.yandex.practicum.order.entity.OrderItem;
import ru.yandex.practicum.order.exception.NotFoundException;
import ru.yandex.practicum.order.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    private final OrderRepository repository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderDto createConfirmed(Order order) {
        order.setStatus(OrderStatus.CONFIRMED);
        order.setStatusDetails("Order was created.");
        order.setCreatedAt(LocalDateTime.now());

        return orderMapper.toDto(repository.save(order));
    }

    @Override
    @Transactional
    public OrderDto createPending(Order order) {
        order.setStatus(OrderStatus.PENDING_CONFIRMATION);
        order.setStatusDetails("Order was created but need underwriting for product data and reserving stock");
        order.setCreatedAt(LocalDateTime.now());

        return orderMapper.toDto(repository.save(order));
    }

    @Override
    public OrderDto getById(Long orderId) {
        return repository.findById(orderId).map(orderMapper::toDto)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Заказа с i = %d не существует", orderId))
                );
    }

    @Override
    public List<OrderDto> getOrders() {
        return repository.findAll().stream().map(orderMapper::toDto).toList();
    }

    @Override
    public List<OrderDto> getByEmail(String email) {
        return repository.findByCustomerEmail(email).stream().map(orderMapper::toDto).toList();
    }
}
