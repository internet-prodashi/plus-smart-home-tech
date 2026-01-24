package ru.yandex.practicum.order.feign;

import feign.Feign;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.interaction.feign.decoder.CustomErrorDecoder;

@Configuration
@EnableFeignClients(basePackages = {"ru.yandex.practicum.interaction"})
public class FeignConfig {
    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .errorDecoder(new CustomErrorDecoder());
    }
}