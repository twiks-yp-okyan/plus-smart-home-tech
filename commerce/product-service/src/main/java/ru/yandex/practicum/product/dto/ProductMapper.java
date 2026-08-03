package ru.yandex.practicum.product.dto;

import org.mapstruct.*;
import ru.yandex.practicum.product.entity.Product;

@Mapper(componentModel = "spring", uses = { CategoryMapper.class })
public interface ProductMapper {
    ProductDto toDto(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "active", ignore = true)
    Product toEntity(CreateProductRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void toEntity(UpdateProductRequest dto, @MappingTarget Product product);
}
