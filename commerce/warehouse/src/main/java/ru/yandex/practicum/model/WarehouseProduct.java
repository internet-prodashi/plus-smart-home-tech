package ru.yandex.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "warehouse_product")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarehouseProduct {
    @Id
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "quantity")
    @Builder.Default
    private Integer quantity = 0;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "fragile")
    private Boolean fragile;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "dimension_id", referencedColumnName = "id", unique = true)
    Dimension dimension;
}
