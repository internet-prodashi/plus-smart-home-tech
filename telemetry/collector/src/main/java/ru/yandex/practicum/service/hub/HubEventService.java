package ru.yandex.practicum.service.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.kafka.KavkaProducerParam;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.HubEventType;

@Slf4j
@RequiredArgsConstructor
public abstract class HubEventService<T extends SpecificRecordBase> {
    protected final KafkaProducerEvent kafkaProducerEvent;
    protected final String topicsNames;

    public void handle(HubEvent hubEvent) {
        if (hubEvent == null)
            throw new IllegalArgumentException("HubEvent cannot be null");

        kafkaProducerEvent.sendRecord(
                KavkaProducerParam.builder()
                        .topic(topicsNames)
                        .timestamp(hubEvent.getTimestamp().toEpochMilli())
                        .key(hubEvent.getHubId())
                        .value(mapToAvroHubEvent(hubEvent))
                        .build()
        );
        log.trace("Record send confirm hubId={}", hubEvent.getHubId());
    }

    public HubEventType getType() {
        throw new UnsupportedOperationException("The method must be redefined in the inheritor.");
    }

    protected HubEventAvro buildHubEventAvro(HubEvent hubEvent, T payloadAvro) {
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(payloadAvro)
                .build();
    }

    protected abstract SpecificRecordBase mapToAvro(HubEvent hubEvent);

    protected abstract HubEventAvro mapToAvroHubEvent(HubEvent hubEvent);
}
