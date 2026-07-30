package ru.yandex.practicum.product.service;

import ru.yandex.practicum.product.dto.CreateProductRequest;
import ru.yandex.practicum.product.dto.ProductDto;
import ru.yandex.practicum.product.dto.UpdateProductRequest;

import java.util.List;

public interface ProductService {
    ProductDto createProduct(CreateProductRequest request);

    ProductDto getProductById(Long id);

    List<ProductDto> getActiveProducts();

    ProductDto updateProduct(Long id, UpdateProductRequest request);

    List<ProductDto> getProductsByCategoryId(Long categoryId);

    List<ProductDto> getProductsByName(String name);
}
