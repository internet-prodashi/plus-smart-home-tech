package ru.yandex.practicum.service.sensor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.kafka.KavkaProducerParam;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.service.mapper.SensorEventAvroMapper;
import ru.yandex.practicum.service.mapper.SensorEventProtoMapper;

import java.time.Instant;

@Slf4j
@RequiredArgsConstructor
public abstract class SensorEventService<T extends SpecificRecordBase> {
    protected final KafkaProducerEvent kafkaProducerEvent;
    protected final String topicsNames;
    protected final SensorEventProtoMapper protoMapper;
    protected final SensorEventAvroMapper avroMapper;

    public void handle(SensorEventProto sensorProto) {
        if (sensorProto == null)
            throw new IllegalArgumentException("HubEvent cannot be null");

        SensorEvent sensor = mapSensorProtoToModel(sensorProto);
        kafkaProducerEvent.sendRecord(KavkaProducerParam.builder()
                .topic(topicsNames)
                .timestamp(sensor.getTimestamp().toEpochMilli())
                .key(sensorProto.getHubId())
                .value(mapSensorEventToAvro(sensor))
                .build()
        );
        log.trace("Record send confirm hubId={}", sensorProto.getHubId());
    }

    public SensorEventProto.PayloadCase getType() {
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

    protected SensorEvent mapBaseSensorProtoFieldsToSensor(SensorEvent sensor, SensorEventProto sensorProto) {
        sensor.setId(sensorProto.getId());
        sensor.setHubId(sensorProto.getHubId());
        long seconds = sensorProto.getTimestamp().getSeconds();
        int nanos = sensorProto.getTimestamp().getNanos();
        sensor.setTimestamp(Instant.ofEpochSecond(seconds, nanos));
        return sensor;
    }

    protected abstract SensorEventAvro mapSensorEventToAvro(SensorEvent sensorEvent);

    protected abstract SensorEvent mapSensorProtoToModel(SensorEventProto sensorProto);
}
