package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final Environment environment;
    private final SnapshotsConsumerProperties snapshotsProps;
    private final HubConsumerProperties hubProps;

    @Bean
    public Consumer<String, SensorsSnapshotAvro> snapshotConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, snapshotsProps.getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, snapshotsProps.getValueDeserializer());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, snapshotsProps.getClientId());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, snapshotsProps.getGroupId());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, snapshotsProps.isEnableAutoCommit());
        return new KafkaConsumer<>(config);
    }

    @Bean
    public Consumer<String, HubEventAvro> hubEventConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getProperty("spring.kafka.bootstrap-servers"));
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, hubProps.getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, hubProps.getValueDeserializer());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, hubProps.getClientId());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, hubProps.getGroupId());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, hubProps.isEnableAutoCommit());
        return new KafkaConsumer<>(config);
    }
}