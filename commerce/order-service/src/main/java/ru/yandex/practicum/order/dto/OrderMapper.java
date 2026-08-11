package ru.yandex.practicum.order.dto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.yandex.practicum.order.entity.Order;

@Mapper(componentModel = "spring", uses = { OrderItemMapper.class })
public interface OrderMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "statusDetails", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "items", ignore = true)
    Order toEntity(CreateOrderRequest dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "totalPrice", source = "totalPrice")
    OrderDto toDto(Order entity);
}
