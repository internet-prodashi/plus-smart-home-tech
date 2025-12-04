package ru.yandex.practicum.service.sensor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

@Component
public class TemperatureSensorEventService extends SensorEventService<TemperatureSensorAvro> {
    public TemperatureSensorEventService(KafkaProducerEvent kafkaProducerEvent, @Value("${topic.sensor-events}") String topicsNames) {
        super(kafkaProducerEvent, topicsNames);
    }

    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }

    public TemperatureSensorAvro mapToAvro(SensorEvent sensorEvent) {
        TemperatureSensorEvent temperatureSensorEvent = (TemperatureSensorEvent) sensorEvent;
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                .build();
    }

    protected SensorEventAvro mapToAvroSensorEvent(SensorEvent sensorEvent) {
        TemperatureSensorAvro avro = mapToAvro(sensorEvent);
        return buildSensorEventAvro(sensorEvent, avro);
    }
}
