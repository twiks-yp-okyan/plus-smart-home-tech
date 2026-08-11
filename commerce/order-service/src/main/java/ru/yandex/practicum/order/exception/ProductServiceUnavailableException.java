package ru.yandex.practicum.order.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductServiceUnavailableException extends RuntimeException {
    private final Long productId;
    private final Throwable cause;
}

