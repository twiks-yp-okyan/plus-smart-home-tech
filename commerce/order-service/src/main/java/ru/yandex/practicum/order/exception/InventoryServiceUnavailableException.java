package ru.yandex.practicum.order.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InventoryServiceUnavailableException extends RuntimeException {
    private final Long productId;
    private final Throwable cause;
}
