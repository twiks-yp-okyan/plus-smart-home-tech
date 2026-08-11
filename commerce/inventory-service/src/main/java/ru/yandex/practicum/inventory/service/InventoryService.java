package ru.yandex.practicum.inventory.service;

import ru.yandex.practicum.inventory.dto.InventoryDto;
import ru.yandex.practicum.inventory.dto.ReserveRequest;
import ru.yandex.practicum.inventory.dto.ReserveResponse;

import java.util.List;

public interface InventoryService {
    InventoryDto createOrUpdate(ReserveRequest request);

    List<InventoryDto> getInventories();

    InventoryDto getInventoryByProductId(Long productId);

    ReserveResponse reserve(ReserveRequest request);

    ReserveResponse release(ReserveRequest request);
}
