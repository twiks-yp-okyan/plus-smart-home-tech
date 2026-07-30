package ru.yandex.practicum.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.product.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByNameAndCategoryId(String name, Long categoryId);

    List<Product> findByActiveTrue();

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByNameContaining(String query);
}
