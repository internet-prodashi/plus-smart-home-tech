package ru.yandex.practicum.interaction.feign;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.payment.PaymentDto;

import java.util.UUID;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentFeignClient {
    @PostMapping
    PaymentDto makingPaymentForOrder(@Valid @RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/totalCost")
    Double calculateTotalCostPayment(@Valid @RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/refund")
    void successfulPayment(@Valid @RequestBody UUID paymentId) throws FeignException;

    @PostMapping("/productCost")
    Double calculateProductCostPayment(@Valid @RequestBody OrderDto orderDto) throws FeignException;

    @PostMapping("/failed")
    void failedPayment(@RequestBody UUID paymentId) throws FeignException;
}