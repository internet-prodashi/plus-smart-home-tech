package ru.yandex.practicum.model.sensor;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class MotionSensorEvent extends SensorEvent {
    @NotNull(message = "Quality link cannot be null")
    private Integer linkQuality;

    @NotNull(message = "Motion cannot be null")
    private Boolean motion;

    @NotNull(message = "Voltage cannot be null")
    private Integer voltage;

    @Override
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
