package ru.yandex.practicum.product.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.product.dto.CategoryDto;
import ru.yandex.practicum.product.dto.CategoryMapper;
import ru.yandex.practicum.product.dto.CreateCategoryRequest;
import ru.yandex.practicum.product.entity.Category;
import ru.yandex.practicum.product.exception.NotFoundException;
import ru.yandex.practicum.product.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository repository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getCategories() {
        return repository.findAll().stream().map(categoryMapper::toDto).toList();
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        return repository.findById(id).map(categoryMapper::toDto)
                .orElseThrow(() -> new NotFoundException(String.format("Категории с id = %d не существует.", id)));
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request) {
        Optional<Category> existingCategory = repository.findByName(request.name());
        if (existingCategory.isPresent()) {
            throw new IllegalArgumentException("Категория с таким названием уже существует.");
        }
        return categoryMapper.toDto(repository.save(categoryMapper.toEntity(request)));
    }
}
