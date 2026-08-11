package ru.yandex.practicum.order.dto;

import org.mapstruct.Mapper;
import ru.yandex.practicum.order.entity.OrderItem;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem entity);
}
