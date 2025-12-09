package ru.yandex.practicum.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.kafka.consumer.hub")
public class HubConsumerProperties {
    private String keyDeserializer;
    private String valueDeserializer;
    private String groupId;
    private String clientId;
    private boolean enableAutoCommit;
}