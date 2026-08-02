package ru.yandex.practicum.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.product.dto.*;
import ru.yandex.practicum.product.entity.Category;
import ru.yandex.practicum.product.entity.Product;
import ru.yandex.practicum.product.exception.NotFoundException;
import ru.yandex.practicum.product.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;
    private final ProductMapper productMapper;
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public ProductDto createProduct(CreateProductRequest request) {
        Optional<Product> existingProduct = repository.findByNameAndCategoryId(request.name(), request.categoryId());
        if (existingProduct.isPresent()) {
            throw new IllegalArgumentException("Товар с таким названием в категории с id = %d уже существует.");
        }
        Category category = categoryMapper.toEntity(categoryService.getCategoryById(request.categoryId()));
        Product newProduct = productMapper.toEntity(request);
        newProduct.setCategory(category);
        return productMapper.toDto(repository.save(newProduct));
    }

    @Override
    public ProductDto getProductById(Long id) {
        return repository.findById(id).map(productMapper::toDto)
                .orElseThrow(() -> new NotFoundException(String.format("Товара с id = %d не существует.", id)));
    }

    @Override
    public List<ProductDto> getActiveProducts() {
        return repository.findByActiveTrue().stream().map(productMapper::toDto).toList();
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductRequest request) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Товара с id = %d не существует.", id)));

        productMapper.toEntity(request, product);
        return productMapper.toDto(repository.save(product));
    }

    @Override
    public List<ProductDto> getProductsByCategoryId(Long categoryId) {
        return repository.findByCategoryId(categoryId).stream().map(productMapper::toDto).toList();
    }

    @Override
    public List<ProductDto> getProductsByName(String name) {
        return repository.findByNameContaining(name).stream().map(productMapper::toDto).toList();
    }
}
