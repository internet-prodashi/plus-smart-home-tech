package ru.yandex.practicum.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.payment.service.PaymentService;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public PaymentDto makingPaymentForOrder(@Valid @RequestBody OrderDto orderDto) {
        log.info("Method makingPaymentForOrder: orderDto = {}", orderDto);
        return paymentService.makingPaymentForOrder(orderDto);
    }

    @PostMapping("/totalCost")
    public Double calculateTotalCostPayment(@Valid @RequestBody OrderDto orderDto) {
        log.info("Method calculateTotalCostPayment: ID = {}", orderDto.getOrderId());
        return paymentService.calculateTotalCostPayment(orderDto);
    }

    @PostMapping("/refund")
    public void successfulPayment(@Valid @RequestBody UUID paymentId) {
        log.info("Method successfulPayment: paymentID = {}", paymentId);
        paymentService.successfulPayment(paymentId);
    }

    @PostMapping("/productCost")
    public Double calculateProductCostPayment(@Valid @RequestBody OrderDto orderDto) {
        log.info("Method calculateProductCostPayment: ID = {}", orderDto.getOrderId());
        return paymentService.calculateProductCostPayment(orderDto);
    }

    @PostMapping("/failed")
    public void failedPayment(@RequestBody UUID paymentId) {
        log.info("Method failedPayment: paymentId {}.", paymentId);
        paymentService.failedPayment(paymentId);
    }
}