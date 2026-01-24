package ru.yandex.practicum.store.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.interaction.dto.enums.ProductCategory;
import ru.yandex.practicum.store.model.Product;

import java.util.List;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Product, UUID> {
    List<Product> findByProductCategory(ProductCategory category, Pageable pageable);
}