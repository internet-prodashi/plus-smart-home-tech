package ru.yandex.practicum.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.dto.order.OrderDto;
import ru.yandex.practicum.interaction.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.dto.store.ProductDto;
import ru.yandex.practicum.interaction.dto.enums.PaymentState;
import ru.yandex.practicum.payment.exception.NoFoundPaymentException;
import ru.yandex.practicum.payment.exception.ImpossibleCalculateCostOrderException;
import ru.yandex.practicum.interaction.feign.OrderFeignClient;
import ru.yandex.practicum.interaction.feign.StoreFeignClient;
import ru.yandex.practicum.payment.mapper.PaymentMapper;
import ru.yandex.practicum.payment.model.Payment;
import ru.yandex.practicum.payment.repository.PaymentRepository;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final StoreFeignClient storeClient;
    private final OrderFeignClient orderClient;
    @Value("${payment.VAT}")
    private Double VAT;

    @Transactional
    public PaymentDto makingPaymentForOrder(OrderDto orderDto) {
        Payment payment = Payment.builder()
                .orderId(orderDto.getOrderId())
                .totalPayment(calculateTotalCostPayment(orderDto))
                .deliveryTotal(orderDto.getDeliveryPrice())
                .feeTotal(calculateProductCostPayment(orderDto) * VAT)
                .state(PaymentState.PENDING)
                .build();
        return paymentMapper.mapToPaymentDto(paymentRepository.save(payment));
    }

    public Double calculateTotalCostPayment(OrderDto orderDto) {
        Double productPrice = orderDto.getProductPrice();
        if (productPrice == null || orderDto.getDeliveryPrice() == null)
            throw new ImpossibleCalculateCostOrderException("The cost of an order with ID = " + orderDto.getOrderId()
                                                            + " cannot be calculated. One of the values is productPrice = "
                                                            + productPrice + " or deliveryPrice = "
                                                            + orderDto.getDeliveryPrice() + " equal to zero");
        return productPrice + productPrice * VAT + orderDto.getDeliveryPrice();
    }

    @Transactional
    public void successfulPayment(UUID paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setState(PaymentState.SUCCESS);
        orderClient.paymentOrder(payment.getOrderId());
    }

    public Double calculateProductCostPayment(OrderDto orderDto) {
        Map<UUID, Integer> products = orderDto.getProducts();

        Map<UUID, Float> price = products.keySet().stream()
                .map(storeClient::getProductById)
                .collect(Collectors.toMap(ProductDto::getProductId, ProductDto::getPrice));

        return products.entrySet().stream()
                .map(entry -> entry.getValue() * price.get(entry.getKey()))
                .mapToDouble(Float::floatValue)
                .sum();
    }

    @Transactional
    public void failedPayment(UUID paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setState(PaymentState.FAILED);
        orderClient.paymentOrderFailed(payment.getOrderId());
    }

    private Payment getPaymentById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NoFoundPaymentException("Payment with ID = " + paymentId + " not found."));
    }
}