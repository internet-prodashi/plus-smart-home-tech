package ru.yandex.practicum.warehouse.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "orders_booked")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderBooking {
    @Id
    @Column(name = "order_id")
    private UUID orderId;

    @ElementCollection
    @CollectionTable(name = "booking_products", joinColumns = @JoinColumn(name = "order_booking_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<UUID, Integer> products;

    @Column(name = "delivery_id")
    private UUID deliveryId;
}