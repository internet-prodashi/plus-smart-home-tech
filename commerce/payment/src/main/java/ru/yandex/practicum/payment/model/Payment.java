package ru.yandex.practicum.payment.model;

import jakarta.persistence.*;
import lombok.*;
import ru.yandex.practicum.interaction.dto.enums.PaymentState;

import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID paymentId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "total_payment")
    private Double totalPayment;

    @Column(name = "delivery_total")
    private Double deliveryTotal;

    @Column(name = "fee_total")
    private Double feeTotal;

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    private PaymentState state;
}