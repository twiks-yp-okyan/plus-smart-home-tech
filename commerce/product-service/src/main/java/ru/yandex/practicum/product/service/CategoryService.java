package ru.yandex.practicum.product.service;

import ru.yandex.practicum.product.dto.CategoryDto;
import ru.yandex.practicum.product.dto.CreateCategoryRequest;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategories();

    CategoryDto getCategoryById(Long id);

    CategoryDto createCategory(CreateCategoryRequest request);
}
