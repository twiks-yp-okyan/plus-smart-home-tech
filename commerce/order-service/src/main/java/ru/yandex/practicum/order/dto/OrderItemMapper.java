package ru.yandex.practicum.order.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.order.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemRequest dto);

    OrderItemDto toDto(OrderItem entity);
}
