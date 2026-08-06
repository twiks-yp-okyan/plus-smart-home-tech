package ru.yandex.practicum.order.feign.product;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.order.feign.fallback.ProductClientFallbackFactory;
import ru.yandex.practicum.order.feign.product.dto.ProductDto;

@FeignClient(
        name = "product-service",
        fallbackFactory = ProductClientFallbackFactory.class
)
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductDto getProductById(@PathVariable("id") Long id);
}