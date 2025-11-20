package ru.yandex.practicum.service.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.kafka.KavkaProducerParam;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;

@Slf4j
@RequiredArgsConstructor
public abstract class SensorEventService<T extends SpecificRecordBase> {
    protected final KafkaProducerEvent kafkaProducerEvent;
    protected final String topicsNames;

    public void handle(SensorEvent sensorEvent) {
        if (sensorEvent == null)
            throw new IllegalArgumentException("HubEvent cannot be null");

        kafkaProducerEvent.sendRecord(KavkaProducerParam.builder()
                .topic(topicsNames)
                .timestamp(sensorEvent.getTimestamp().toEpochMilli())
                .key(sensorEvent.getHubId())
                .value(mapToAvroSensorEvent(sensorEvent))
                .build()
        );
        log.trace("Record send confirm hubId={}", sensorEvent.getHubId());
    }

    public SensorEventType getType() {
        throw new UnsupportedOperationException("The method must be redefined in the inheritor.");
    }

    protected SensorEventAvro buildSensorEventAvro(SensorEvent sensorEvent, T payloadAvro) {
        return SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(payloadAvro)
                .build();
    }

    protected abstract SpecificRecordBase mapToAvro(SensorEvent sensorEvent);

    protected abstract SensorEventAvro mapToAvroSensorEvent(SensorEvent sensorEvent);
}
