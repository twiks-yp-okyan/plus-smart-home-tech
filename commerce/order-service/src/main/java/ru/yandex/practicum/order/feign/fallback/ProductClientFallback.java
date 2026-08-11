package ru.yandex.practicum.order.feign.fallback;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.order.exception.OrderProcessingException;
import ru.yandex.practicum.order.exception.ProductServiceUnavailableException;
import ru.yandex.practicum.order.feign.product.ProductClient;
import ru.yandex.practicum.order.feign.product.dto.ProductDto;

@RequiredArgsConstructor
@Slf4j
public class ProductClientFallback implements ProductClient {
    private final Throwable cause;

    @Override
    public ProductDto getProductById(@PathVariable("id") Long id) {
        if (cause instanceof FeignException.NotFound) {
            log.info(
                    "Нет информации о товаре id={}",
                    id,
                    cause
            );
            throw new OrderProcessingException(cause.getMessage());
        }

        if (cause instanceof CallNotPermittedException) {
            log.warn("Circuit Breaker OPEN - запросы к product-service не выполняются!", cause);
        }

        log.warn(
                "product-service недоступен при запросе товара id={}",
                id,
                cause
        );
        throw new ProductServiceUnavailableException(id, cause);
    }
}
