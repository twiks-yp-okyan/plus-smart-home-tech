package ru.yandex.practicum.product.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.product.dto.CreateProductRequest;
import ru.yandex.practicum.product.dto.ProductDto;
import ru.yandex.practicum.product.dto.UpdateProductRequest;
import ru.yandex.practicum.product.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto createProduct(@Valid @RequestBody CreateProductRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable long id) {
        return productService.getProductById(id);
    }

    @GetMapping
    public List<ProductDto> getAllActive() {
        return productService.getActiveProducts();
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductDto> getByCategoryId(@PathVariable long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @GetMapping("/search")
    public List<ProductDto> getByName (@RequestParam(value = "query") String query) {
        return productService.getProductsByName(query);
    }

    @PatchMapping("/{id}")
    public ProductDto updateProduct(
            @PathVariable long id,
            @Valid @RequestBody UpdateProductRequest request
            ) {
        return productService.updateProduct(id, request);
    }
}
