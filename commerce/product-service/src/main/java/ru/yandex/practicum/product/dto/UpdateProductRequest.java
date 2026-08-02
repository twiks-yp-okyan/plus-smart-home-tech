package ru.yandex.practicum.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
        @Size(max = 255, message = "Название не может быть длиннее 255 символов")
        private String name;

        @Size(max = 2000, message = "Описание не может быть длиннее 2000 символов")
        private String description;

        @DecimalMin(value = "0.01", message = "Цена должна быть больше нуля")
        private BigDecimal price;

        private Long categoryId;

        private String imageUrl;

        private Boolean active;
}