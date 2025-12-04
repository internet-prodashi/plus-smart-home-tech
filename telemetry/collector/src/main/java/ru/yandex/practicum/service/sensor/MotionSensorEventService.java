package ru.yandex.practicum.service.sensor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.kafka.KafkaProducerEvent;

@Component
public class MotionSensorEventService extends SensorEventService<MotionSensorAvro> {
    public MotionSensorEventService(KafkaProducerEvent kafkaProducerEvent, @Value("${topic.sensor-events}") String topicsNames) {
        super(kafkaProducerEvent, topicsNames);
    }

    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }

    public MotionSensorAvro mapToAvro(SensorEvent sensorEvent) {
        MotionSensorEvent motionSensorEvent = (MotionSensorEvent) sensorEvent;
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(motionSensorEvent.getLinkQuality())
                .setMotion(motionSensorEvent.getMotion())
                .setVoltage(motionSensorEvent.getVoltage())
                .build();
    }

    protected SensorEventAvro mapToAvroSensorEvent(SensorEvent sensorEvent) {
        MotionSensorAvro avro = mapToAvro(sensorEvent);
        return buildSensorEventAvro(sensorEvent, avro);
    }
}