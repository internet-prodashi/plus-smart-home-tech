package ru.yandex.practicum.kafka;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.SnapshotService;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private final Consumer<String, SensorsSnapshotAvro> consumer;
    private final SnapshotService snapshotHandler;
    private volatile boolean isRunning = true;

    @Value("${analyzer.topic.snapshots-topic}")
    private String topic;

    public void start() {
        consumer.subscribe(List.of(topic));
        log.info("Subscribed to the topic {}", topic);
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

        try {
            while (isRunning) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                    handleRecord(record);
                }
                if (!records.isEmpty()) consumer.commitSync();
            }
            log.info("The PoolLoop is stopped manually");
        } catch (WakeupException ignored) {
            log.warn("Arose WakeupException");
        } catch (Exception exp) {
            log.error("Error reading data from a topic {}", topic, exp);
        } finally {
            try {
                log.info("Closing Consumer");
                consumer.close();
            } catch (Exception exp) {
                log.warn("Error when closing CONSUMER", exp);
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        consumer.wakeup();
        isRunning = false;
    }

    private void handleRecord(ConsumerRecord<String, SensorsSnapshotAvro> record) {
        SensorsSnapshotAvro snapshot = record.value();
        log.info("Got a SNAPSHOT of the smart home status: {}", snapshot);
        snapshotHandler.handle(snapshot);
    }
}
