package ru.yandex.practicum.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ClimateSensorEvent extends SensorEvent {
    @NotNull(message = "The temperature cannot be null")
    private Integer temperatureC;

    @NotNull(message = "Humidity cannot be null")
    private Integer humidity;

    @NotNull(message = "The carbon dioxide level cannot be null")
    private Integer co2Level;

    @Override
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}