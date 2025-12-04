package ru.yandex.practicum.model.hub;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class ScenarioCondition {
    @NotBlank(message = "ID sensor cannot be empty")
    private String sensorId;

    @NotNull(message = "Type cannot be null")
    private ConditionType type;

    @NotNull(message = "Operation cannot be null")
    private ConditionOperation operation;

    @NotNull(message = "Value cannot be null")
    private Integer value;
}
