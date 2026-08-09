package ru.yandex.practicum.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.inventory.dto.InventoryDto;
import ru.yandex.practicum.inventory.dto.InventoryMapper;
import ru.yandex.practicum.inventory.dto.ReserveRequest;
import ru.yandex.practicum.inventory.dto.ReserveResponse;
import ru.yandex.practicum.inventory.entity.Inventory;
import ru.yandex.practicum.inventory.exception.InsufficientStockException;
import ru.yandex.practicum.inventory.exception.NotFoundException;
import ru.yandex.practicum.inventory.repository.InventoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository repository;
    private final InventoryMapper inventoryMapper;

    private final Integer ZERO = 0;

    @Override
    @Transactional
    public InventoryDto createOrUpdate(ReserveRequest request) {
        Inventory inventory;

        Optional<Inventory> existingInventory = repository.findByProductId(request.productId());
        if (existingInventory.isEmpty()) {
            // create new
            inventory = inventoryMapper.toNewEntity(request);
            inventory.setAvailableQuantity(request.quantity());
            inventory.setReservedQuantity(ZERO);
        } else {
            // update existing
            inventory = existingInventory.get();
            Integer newAvailableQuantity = request.quantity() - inventory.getReservedQuantity();
            inventory.setAvailableQuantity(newAvailableQuantity);
            inventory.setQuantity(request.quantity());
        }
        return inventoryMapper.toDto(repository.save(inventory));
    }

    @Override
    public List<InventoryDto> getInventories() {
        return repository.findAll().stream().map(inventoryMapper::toDto).toList();
    }

    @Override
    public InventoryDto getInventoryByProductId(Long productId) {
        return repository.findByProductId(productId).map(inventoryMapper::toDto)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Записи с productId = %d не существует.", productId))
                );
    }

    @Override
    @Transactional
    public ReserveResponse reserve(ReserveRequest request) {
        Inventory existingInventory = repository.findByProductId(request.productId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Записи с productId = %d не существует.", request.productId())
                ));

        if (compareQuantities(existingInventory.getAvailableQuantity(), request.quantity())) {
            Integer newAvailableQuantity = existingInventory.getAvailableQuantity() - request.quantity();
            Integer newReservedQuantity = existingInventory.getReservedQuantity() + request.quantity();
            existingInventory.setAvailableQuantity(newAvailableQuantity);
            existingInventory.setReservedQuantity(newReservedQuantity);
            Inventory updatedInventory = repository.save(existingInventory);
            return buildResponse(updatedInventory, "Товар успешно зарезервирован");
        } else {
            throw new InsufficientStockException(
                    String.format("Запрошенное количество превышает доступный остаток для товара productId = %d",
                            request.productId())
            );
        }
    }

    @Override
    @Transactional
    public ReserveResponse release(ReserveRequest request) {
        Inventory existingInventory = repository.findByProductId(request.productId())
                .orElseThrow(() -> new NotFoundException(
                        String.format("Записи с productId = %d не существует.", request.productId())
                ));

        if (compareQuantities(existingInventory.getReservedQuantity(), request.quantity())) {
            Integer newAvailableQuantity = existingInventory.getAvailableQuantity() + request.quantity();
            Integer newReservedQuantity = existingInventory.getReservedQuantity() - request.quantity();
            existingInventory.setAvailableQuantity(newAvailableQuantity);
            existingInventory.setReservedQuantity(newReservedQuantity);
            Inventory updatedInventory = repository.save(existingInventory);
            return buildResponse(updatedInventory, "Товар успешно снят с резерва");
        } else {
            throw new IllegalArgumentException(
                    String.format("Запрошенное количество превышает резерв для товара productId = %d",
                            request.productId())
            );
        }
    }

    private boolean compareQuantities(Integer available, Integer requested) {
        return available.compareTo(requested) >= 0;
    }

    private ReserveResponse buildResponse(Inventory inventory, String message) {
        return new ReserveResponse(true, inventory.getAvailableQuantity(), message);
    }
}
