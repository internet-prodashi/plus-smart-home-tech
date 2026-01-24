package ru.yandex.practicum.feign;

import feign.Feign;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.feign.decoder.CustomErrorDecoder;

@Configuration
@EnableFeignClients(basePackages = {"ru.yandex.practicum"})
public class FeignConfig {
    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder().errorDecoder(new CustomErrorDecoder());
    }
}
