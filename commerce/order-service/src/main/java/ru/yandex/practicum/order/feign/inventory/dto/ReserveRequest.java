package ru.yandex.practicum.order.feign.inventory.dto;

public record ReserveRequest(
        Long productId,
        Integer quantity
) {
}