package ru.yandex.practicum.service.sensor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.LightSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.service.mapper.SensorEventAvroMapper;
import ru.yandex.practicum.service.mapper.SensorEventProtoMapper;

@Component
public class LightSensorEventService extends SensorEventService<LightSensorAvro> {
    public LightSensorEventService(
            KafkaProducerEvent kafkaProducerEvent,
            @Value("${topic.sensor-events}") String topicsNames,
            SensorEventProtoMapper protoMapper,
            SensorEventAvroMapper avroMapper
    ) {
        super(kafkaProducerEvent, topicsNames, protoMapper, avroMapper);
    }

    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.LIGHT_SENSOR_EVENT;
    }

    @Override
    protected SensorEventAvro mapSensorEventToAvro(SensorEvent sensorEvent) {
        return buildSensorEventAvro(
                sensorEvent,
                avroMapper.mapLightSensorToAvro((LightSensorEvent) sensorEvent)
        );
    }

    @Override
    protected SensorEvent mapSensorProtoToModel(SensorEventProto sensorProto) {
        return mapBaseSensorProtoFieldsToSensor(
                protoMapper.mapLightSensorProtoToModel(sensorProto.getLightSensorEvent()),
                sensorProto
        );
    }
}