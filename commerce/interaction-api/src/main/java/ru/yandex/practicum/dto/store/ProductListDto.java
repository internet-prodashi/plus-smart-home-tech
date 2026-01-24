package ru.yandex.practicum.dto.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ProductListDto {
    private List<ProductDto> content;

    private Sort sort;
}