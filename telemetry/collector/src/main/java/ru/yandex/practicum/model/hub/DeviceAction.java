package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeviceAction {
    @NotBlank(message = "The sensor ID cannot be empty")
    private String sensorId;

    @NotNull(message = "ActionType cannot be null")
    private ActionType type;

    private Integer value;
}
