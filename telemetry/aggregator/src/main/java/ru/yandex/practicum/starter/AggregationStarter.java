package ru.yandex.practicum.starter;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.service.SnapshotService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {
    private final Consumer<String, SpecificRecordBase> consumer;
    private final Producer<String, SpecificRecordBase> producer;
    private final SnapshotService snapshots;
    private final Map<TopicPartition, OffsetAndMetadata> offsets = new HashMap<>();

    @Value("${topic.telemetry-sensors}")
    private String sensorsTopic;

    @Value("${topic.telemetry-snapshots}")
    private String snapshotsTopic;

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(sensorsTopic));

            while (!Thread.currentThread().isInterrupted()) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));
                int count = 0;
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    log.info("Processing the message: topic = {}, partition = {}, offset = {}, value: {}",
                            record.topic(), record.partition(), record.offset(), record.value());

                    SensorEventAvro event = (SensorEventAvro) record.value();
                    Optional<SensorsSnapshotAvro> snapshot = snapshots.updateState(event);

                    if (snapshot.isPresent()) {
                        producer.send(new ProducerRecord<>(
                                snapshotsTopic,
                                null,
                                event.getTimestamp().toEpochMilli(),
                                event.getHubId(),
                                snapshot.get()
                        ));
                        log.info("Snapshot updated: {}", snapshot);
                    } else {
                        log.info("Snapshot not updated");
                    }

                    offsets.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                    );

                    if (count % 10 == 0) {
                        consumer.commitAsync(offsets, (offsets, exception) -> {
                            if (exception != null)
                                log.warn("Error during commit offsets: {}", offsets, exception);
                        });
                    }

                    count++;
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignores) {
        } catch (Exception e) {
            log.error("Error during processing of events from sensors", e);
        } finally {
            try {
                producer.flush();
                consumer.commitSync();
            } finally {
                log.info("Closing consumer and producer");
                consumer.close();
                producer.close();
            }
        }
    }
}
