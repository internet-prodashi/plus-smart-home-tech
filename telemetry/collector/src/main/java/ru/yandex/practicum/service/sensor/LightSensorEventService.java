package ru.yandex.practicum.service.sensor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.LightSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.kafka.KafkaProducerEvent;

@Component
public class LightSensorEventService extends SensorEventService<LightSensorAvro> {
    public LightSensorEventService(KafkaProducerEvent kafkaProducerEvent, @Value("${topic.sensor-events}") String topicsNames) {
        super(kafkaProducerEvent, topicsNames);
    }

    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }

    public LightSensorAvro mapToAvro(SensorEvent sensorEvent) {
        LightSensorEvent lightSensorEvent = (LightSensorEvent) sensorEvent;
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }

    protected SensorEventAvro mapToAvroSensorEvent(SensorEvent sensorEvent) {
        LightSensorAvro avro = mapToAvro(sensorEvent);
        return buildSensorEventAvro(sensorEvent, avro);
    }
}