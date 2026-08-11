package ru.yandex.practicum.order.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.feign.inventory.InventoryClient;

@Component
@Slf4j
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryClient> {
    @Override
    public InventoryClient create(Throwable cause) {
        return new InventoryClientFallback(cause);
    }
}
