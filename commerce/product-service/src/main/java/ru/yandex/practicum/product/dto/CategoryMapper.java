package ru.yandex.practicum.product.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.product.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto dto);

    @Mapping(target = "id", ignore = true)
    Category toEntity(CreateCategoryRequest request);
}
