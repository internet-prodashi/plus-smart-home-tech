package ru.yandex.practicum.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
    private final Environment environment;
    private final KafkaConsumerProperties kafkaConsumerProps;

    @Bean
    public KafkaConsumer<String, SpecificRecordBase> getConsumer() {
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProps.getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, kafkaConsumerProps.getKeyDeserializer());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, kafkaConsumerProps.getValueDeserializer());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProps.getGroupId());
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaConsumerProps.getClientId());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaConsumerProps.getEnableAutoCommit());
        return new KafkaConsumer<>(config);
    }
}