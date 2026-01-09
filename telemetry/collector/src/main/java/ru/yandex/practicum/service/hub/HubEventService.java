package ru.yandex.practicum.service.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.kafka.KavkaProducerParam;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.service.mapper.HubEventAvroMapper;
import ru.yandex.practicum.service.mapper.HubEventProtoMapper;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class HubEventService<T extends SpecificRecordBase> {
    protected final KafkaProducerEvent kafkaProducerEvent;
    protected final String topicsNames;
    protected final HubEventProtoMapper protoMapper;
    protected final HubEventAvroMapper avroMapper;

    public void handle(HubEventProto hubProto) {
        if (hubProto == null)
            throw new IllegalArgumentException("HubEvent cannot be null");

        HubEvent event = mapHubProtoToModel(hubProto);
        kafkaProducerEvent.sendRecord(
                KavkaProducerParam.builder()
                        .topic(topicsNames)
                        .timestamp(event.getTimestamp().toEpochMilli())
                        .key(event.getHubId())
                        .value(mapHubToAvro(event))
                        .build()
        );
        log.trace("Record send confirm hubId={}", hubProto.getHubId());
    }

    public HubEventProto.PayloadCase getType() {
        throw new UnsupportedOperationException("The method must be redefined in the inheritor.");
    }

    protected HubEventAvro buildHubEventAvro(HubEvent hubEvent, T payloadAvro) {
        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(payloadAvro)
                .build();
    }

    protected HubEvent mapBaseHubProtoFieldsToHub(HubEvent hub, HubEventProto hubProto) {
        hub.setHubId(hubProto.getHubId());
        long seconds = hubProto.getTimestamp().getSeconds();
        int nanos = hubProto.getTimestamp().getNanos();
        hub.setTimestamp(Instant.ofEpochSecond(seconds, nanos));
        return hub;
    }

    protected abstract HubEventAvro mapHubToAvro(HubEvent hubEvent);

    protected abstract HubEvent mapHubProtoToModel(HubEventProto hubProto);
}
