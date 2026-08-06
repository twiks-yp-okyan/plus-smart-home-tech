package ru.yandex.practicum.order.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.order.dto.*;
import ru.yandex.practicum.order.entity.Order;
import ru.yandex.practicum.order.entity.OrderItem;
import ru.yandex.practicum.order.exception.InventoryServiceUnavailableException;
import ru.yandex.practicum.order.exception.OrderProcessingException;
import ru.yandex.practicum.order.exception.ProductServiceUnavailableException;
import ru.yandex.practicum.order.feign.inventory.InventoryClient;
import ru.yandex.practicum.order.feign.inventory.dto.ReserveRequest;
import ru.yandex.practicum.order.feign.inventory.dto.ReserveResponse;
import ru.yandex.practicum.order.feign.product.ProductClient;
import ru.yandex.practicum.order.feign.product.dto.ProductDto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderOrchestrationServiceImpl implements OrderOrchestrationService {
    private final OrderService orderService;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final OrderMapper orderMapper;

    @Override
    public OrderDto createOrder(CreateOrderRequest request) {
        log.debug("Создаем мапу с итоговым количеством для каждого товара");
        Map<Long, Integer> orderItemTotalQuantity = getOrderItemTotalQuantity(request.items());
        Map<Long, Integer> reservedProductIds = new HashMap<>();

        Order order = orderMapper.toEntity(request);
        log.debug("Маппинг заказа order-маппером");
        try {
            log.debug("Создаем мапу с инфой о товарах для исключения повторных обращений к product-service");
            Map<Long, ProductDto> orderItemsProductData = getOrderItemsProductData(request.items());

            orderItemsProductData.forEach((productId, productData) -> {
                Integer quantity = orderItemTotalQuantity.get(productId);
                log.debug("Создаем Entity OrderItem для товара с id = {}", productId);
                OrderItem item = createOrderItem(productData, quantity);
                log.debug("Попытка зарезервивовать товар c id = {} в количестве - {}", productId, quantity);
                ReserveResponse reserveResponse = reserveProductQuantity(productId, quantity);
                if (reserveResponse.success()) {
                    log.debug("Успешный резерв товара c id = {} в количестве - {}", productId, quantity);
                    reservedProductIds.put(productId, orderItemTotalQuantity.get(productId));
                }
                log.debug("Добавляем позицию в заказ с товаром - {} в количестве - {}", productId, quantity);
                order.addItem(item);
            });
        } catch (OrderProcessingException e) {
            log.debug("Бизнес-отказ: {}\nНачинаем отменять зарезервированные товары.", e.getMessage());
            try {
                reservedProductIds.forEach((productId, quantity) -> {
                    inventoryClient.releaseStock(new ReserveRequest(productId, quantity));
                    log.debug("Отменяем позицию товара - {} в количестве - {}", productId, quantity);
                });
                log.debug("Отмена резервирования прошла успешно");
            } catch (FeignException.BadRequest ex) {
                log.warn("Неудачная попытка отменить резервинование товаров");
            }
            throw new OrderProcessingException(e.getMessage());
        } catch (ProductServiceUnavailableException | InventoryServiceUnavailableException unavailableEx) {
            log.warn("Сценарий создания заказа при недоступности внешних сервисов.");
            request.items().forEach(orderItemRequest -> {
                log.debug("Создаем fallback-Entity OrderItem для товара с id = {}", orderItemRequest.productId());
                OrderItem item = createOrderItemFallback(orderItemRequest.productId(), orderItemRequest.quantity());
                log.debug("Добавляем fallback-позицию в заказ с товаром - {} в количестве - {}", orderItemRequest.productId(), orderItemRequest.quantity());
                order.addItem(item);
            });
            return orderService.createPending(order);
        }
        return orderService.createConfirmed(order);
    }

    private OrderItem createOrderItem(ProductDto productData, Integer quantity) {
        if (!productData.active()) {
            throw new OrderProcessingException(String.format("Товар с id = %d не доступен для заказа", productData.id()));
        }
        OrderItem item = new OrderItem();
        item.setProductId(productData.id());
        item.setProductName(productData.name());
        item.setQuantity(quantity);
        item.setPrice(productData.price());
        return item;
    }

    private OrderItem createOrderItemFallback(Long productId, Integer quantity) {
        OrderItem item = new OrderItem();
        item.setProductId(productId);
        item.setQuantity(quantity);
        item.setPrice(BigDecimal.ZERO);
        item.setProductName("Имя товара необходимо уточнить");
        return item;
    }

    private Map<Long, ProductDto> getOrderItemsProductData(List<OrderItemRequest> itemsDto) {
        Map<Long, ProductDto> orderItemsProductData = new HashMap<>();
        itemsDto.forEach(itemDto -> {
            if (!orderItemsProductData.containsKey(itemDto.productId())) {
                orderItemsProductData.put(
                        itemDto.productId(),
                        getProductData(itemDto.productId())
                );
            }
        });
        return orderItemsProductData;
    }

    private Map<Long, Integer> getOrderItemTotalQuantity(List<OrderItemRequest> itemsDto) {
        Map<Long, Integer> orderItemTotalQuantity = new HashMap<>();
        itemsDto.forEach(itemDto -> {
            Integer quantity;
            if (orderItemTotalQuantity.containsKey(itemDto.productId())) {
                quantity = orderItemTotalQuantity.get(itemDto.productId()) + itemDto.quantity();
            } else {
                quantity = itemDto.quantity();
            }
            orderItemTotalQuantity.put(itemDto.productId(), quantity);
        });
        return orderItemTotalQuantity;
    }

    private ProductDto getProductData(Long productId) {
        return productClient.getProductById(productId);
    }

    private ReserveResponse reserveProductQuantity(Long productId, Integer quantity) {
        return inventoryClient.reserveStock(new ReserveRequest(productId, quantity));
    }
}
