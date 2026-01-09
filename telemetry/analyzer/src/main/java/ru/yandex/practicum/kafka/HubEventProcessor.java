package ru.yandex.practicum.kafka;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.service.HubEventService;
import ru.yandex.practicum.service.HubEventServiceMap;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final Consumer<String, HubEventAvro> consumer;
    private final HubEventServiceMap hubEventServiceMap;
    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();
    private volatile boolean isRunning = true;

    @Value("${analyzer.topic.hub-event-topic}")
    private String topic;

    @Override
    public void run() {
        try {
            consumer.subscribe(List.of(topic));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
            Map<String, HubEventService> handlerMap = hubEventServiceMap.getHubMap();

            while (isRunning) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(100));
                int count = 0;

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    handleRecord(record, handlerMap);
                    manageOffsets(record, count);
                    count++;
                }
                consumer.commitAsync();
            }
            log.info("The PoolLoop is stopped manually");
        } catch (WakeupException ignored) {
            log.warn("Arose WakeupException");
        } catch (Exception exp) {
            log.error("Error reading data from a topic {}", topic);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                try {
                    log.info("Closing CONSUMER");
                    consumer.close();
                } catch (Exception exp) {
                    log.warn("Error when closing CONSUMER", exp);
                }
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        consumer.wakeup();
        isRunning = false;
    }

    private void handleRecord(ConsumerRecord<String, HubEventAvro> record, Map<String, HubEventService> handlerMap) {
        HubEventAvro event = record.value();
        String payloadName = event.getPayload().getClass().getSimpleName();
        log.info("We received a message from a HUB like: {}", payloadName);

        if (handlerMap.containsKey(payloadName)) {
            handlerMap.get(payloadName).handle(event);
        } else {
            throw new IllegalArgumentException("The HANDLER was not found for the event: " + event);
        }
    }

    private void manageOffsets(ConsumerRecord<String, HubEventAvro> record, int count) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1));

        if (count % 2 == 0) {
            log.debug("count={}", count);
            OptionalLong maxOptional = currentOffsets.values().stream()
                    .mapToLong(OffsetAndMetadata::offset)
                    .max();
            maxOptional.ifPresent(max -> log.debug("Fixing OFFSET max={}", max));

            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception == null) {
                    log.debug("Successful fixation OFFSETS: {}", offsets);
                } else {
                    log.error("Error during commit OFFSETS: {}", offsets, exception);
                }
            });
        }
    }
}