package ru.yandex.practicum.order.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.feign.product.ProductClient;

@Component
@Slf4j
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {
    @Override
    public ProductClient create(Throwable cause) {
        log.error("Product Client fallback: {}", cause.getMessage());
        return new ProductClientFallback(cause);
    }
}
