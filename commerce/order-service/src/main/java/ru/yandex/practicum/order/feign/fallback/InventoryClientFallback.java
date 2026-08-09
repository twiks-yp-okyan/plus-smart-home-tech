package ru.yandex.practicum.order.feign.fallback;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.order.exception.InventoryServiceUnavailableException;
import ru.yandex.practicum.order.exception.OrderProcessingException;
import ru.yandex.practicum.order.feign.inventory.InventoryClient;
import ru.yandex.practicum.order.feign.inventory.dto.ReserveRequest;
import ru.yandex.practicum.order.feign.inventory.dto.ReserveResponse;

@RequiredArgsConstructor
@Slf4j
public class InventoryClientFallback implements InventoryClient {
    private final Throwable cause;

    @Override
    public ReserveResponse reserveStock(ReserveRequest request) {
        if (cause instanceof FeignException.Conflict) {
            log.info(
                    "Запрошенное количество для резерва превышает остатки для товара id={}",
                    request.productId(),
                    cause
            );
            throw new OrderProcessingException(cause.getMessage());
        }

        if (cause instanceof CallNotPermittedException) {
            log.warn("Circuit Breaker OPEN - запросы к product-service не выполняются!", cause);
        }

        log.warn(
                "inventory-service недоступен при резервировании товара id={}",
                request.productId(),
                cause
        );
        throw new InventoryServiceUnavailableException(request.productId(), cause);
    }

    @Override
    public ReserveResponse releaseStock(ReserveRequest request) {
        if (cause instanceof FeignException.Conflict) {
            log.info(
                    "Неудачная попытка отменить резерв для товара id={}",
                    request.productId(),
                    cause
            );
            throw new OrderProcessingException(cause.getMessage());
        }

        log.warn(
                "inventory-service недоступен при снятии резерва товара id={}",
                request.productId(),
                cause
        );

        throw new InventoryServiceUnavailableException(request.productId(), cause);
    }
}
