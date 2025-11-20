package ru.yandex.practicum.service.sensor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;

@Component
public class SwitchSensorEventService extends SensorEventService<SwitchSensorAvro> {
    public SwitchSensorEventService(KafkaProducerEvent kafkaProducerEvent, @Value("${topic.sensor-events}") String topicsNames) {
        super(kafkaProducerEvent, topicsNames);
    }

    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }

    public SwitchSensorAvro mapToAvro(SensorEvent sensorEvent) {
        SwitchSensorEvent switchSensorEvent = (SwitchSensorEvent) sensorEvent;
        return SwitchSensorAvro.newBuilder()
                .setState(switchSensorEvent.getState())
                .build();
    }

    protected SensorEventAvro mapToAvroSensorEvent(SensorEvent sensorEvent) {
        SwitchSensorAvro avro = mapToAvro(sensorEvent);
        return buildSensorEventAvro(sensorEvent, avro);
    }
}