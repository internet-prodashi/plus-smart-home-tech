package ru.yandex.practicum.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@ConfigurationPropertiesScan({"ru.yandex.practicum.interaction", "ru.yandex.practicum.payment"})
public class Payment {
    public static void main(String[] args) {
        SpringApplication.run(Payment.class, args);
    }
}