package ru.yandex.practicum.service.sensor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;
import ru.yandex.practicum.service.mapper.SensorEventAvroMapper;
import ru.yandex.practicum.service.mapper.SensorEventProtoMapper;

@Component
public class TemperatureSensorEventService extends SensorEventService<TemperatureSensorAvro> {
    public TemperatureSensorEventService(
            KafkaProducerEvent kafkaProducerEvent,
            @Value("${topic.sensor-events}") String topicsNames,
            SensorEventProtoMapper protoMapper,
            SensorEventAvroMapper avroMapper
    ) {
        super(kafkaProducerEvent, topicsNames, protoMapper, avroMapper);
    }

    public SensorEventProto.PayloadCase getType() {
        return SensorEventProto.PayloadCase.TEMPERATURE_SENSOR_EVENT;
    }

    @Override
    protected SensorEventAvro mapSensorEventToAvro(SensorEvent sensorEvent) {
        return buildSensorEventAvro(
                sensorEvent,
                avroMapper.mapTemperatureSensor((TemperatureSensorEvent) sensorEvent)
        );
    }

    @Override
    protected SensorEvent mapSensorProtoToModel(SensorEventProto sensorProto) {
        return mapBaseSensorProtoFieldsToSensor(
                protoMapper.mapTemperatureSensorProtoToModel(sensorProto.getTemperatureSensorEvent()),
                sensorProto
        );
    }
}
