package ru.yandex.practicum.interaction.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PaymentDto {
    @NotNull
    private UUID paymentId;

    private Double totalPayment;

    private Double deliveryTotal;

    private Double feeTotal;
}