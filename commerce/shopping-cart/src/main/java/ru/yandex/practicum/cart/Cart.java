package ru.yandex.practicum.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@ConfigurationPropertiesScan({"ru.yandex.practicum.interaction", "ru.yandex.practicum.cart"})
public class Cart {
    public static void main(String[] args) {
        SpringApplication.run(Cart.class, args);
    }
}