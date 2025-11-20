package ru.yandex.practicum.service.sensor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.kafka.KafkaProducerEvent;

@Component
public class ClimateSensorEventService extends SensorEventService<ClimateSensorAvro> {
    public ClimateSensorEventService(KafkaProducerEvent kafkaProducerEvent, @Value("${topic.sensor-events}") String topicsNames) {
        super(kafkaProducerEvent, topicsNames);
    }

    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }

    public ClimateSensorAvro mapToAvro(SensorEvent sensorEvent) {
        ClimateSensorEvent climateSensorEvent = (ClimateSensorEvent) sensorEvent;
        return ClimateSensorAvro.newBuilder()
                .setTemperatureC(climateSensorEvent.getTemperatureC())
                .setHumidity(climateSensorEvent.getHumidity())
                .setCo2Level(climateSensorEvent.getCo2Level())
                .build();
    }

    protected SensorEventAvro mapToAvroSensorEvent(SensorEvent sensorEvent) {
        ClimateSensorAvro avro = mapToAvro(sensorEvent);
        return buildSensorEventAvro(sensorEvent, avro);
    }
}
