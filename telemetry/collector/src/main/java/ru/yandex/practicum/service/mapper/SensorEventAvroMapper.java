package ru.yandex.practicum.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.sensor.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SensorEventAvroMapper {
    ClimateSensorAvro mapClimateSensorToAvro(ClimateSensorEvent climateSensorEvent);

    LightSensorAvro mapLightSensorToAvro(LightSensorEvent lightSensorEvent);

    MotionSensorAvro mapMotionSensorToAvro(MotionSensorEvent motionSensorEvent);

    SwitchSensorAvro mapSwitchSensorToAvro(SwitchSensorEvent switchSensorEvent);

    TemperatureSensorAvro mapTemperatureSensor(TemperatureSensorEvent temperatureSensorEvent);
}
