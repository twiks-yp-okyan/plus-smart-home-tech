package ru.yandex.practicum.order.feign.fallback;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.order.exception.OrderProcessingException;
import ru.yandex.practicum.order.exception.ProductServiceUnavailableException;
import ru.yandex.practicum.order.feign.product.ProductClient;
import ru.yandex.practicum.order.feign.product.dto.ProductDto;

@Component
@Slf4j
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {
    @Override
    public ProductClient create(Throwable cause) {
        return new ProductClient() {
            @Override
            public ProductDto getProductById(Long id) {
                if (cause instanceof FeignException.NotFound) {
                    log.info(
                            "Нет информации о товаре id={}",
                            id,
                            cause
                    );
                    throw new OrderProcessingException(cause.getMessage());
                }

                log.warn(
                        "product-service недоступен при запросе товара id={}",
                        id,
                        cause
                );
                throw new ProductServiceUnavailableException(id, cause);
            }
        };
    }
}
