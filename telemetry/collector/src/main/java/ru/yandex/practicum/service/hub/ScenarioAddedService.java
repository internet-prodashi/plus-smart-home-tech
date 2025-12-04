package ru.yandex.practicum.service.hub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.hub.*;

import java.util.List;

@Component
public class ScenarioAddedService extends HubEventService<ScenarioAddedEventAvro> {
    public ScenarioAddedService(KafkaProducerEvent kafkaProducerEvent, @Value("${topic.hub-events}") String topicsNames) {
        super(kafkaProducerEvent, topicsNames);
    }

    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }

    public ScenarioAddedEventAvro mapToAvro(HubEvent hubEvent) {
        ScenarioAddedEvent scenarioAddedEvent = (ScenarioAddedEvent) hubEvent;
        List<ScenarioConditionAvro> scenarioConditionAvroList = scenarioAddedEvent.getConditions().stream()
                .map(this::mapToScenarioConditionAvro)
                .toList();
        List<DeviceActionAvro> actionAvroList = scenarioAddedEvent.getActions().stream()
                .map(this::mapToDeviceActionAvro)
                .toList();
        return ScenarioAddedEventAvro.newBuilder()
                .setName(scenarioAddedEvent.getName())
                .setConditions(scenarioConditionAvroList)
                .setActions(actionAvroList)
                .build();
    }

    protected HubEventAvro mapToAvroHubEvent(HubEvent hubEvent) {
        ScenarioAddedEventAvro avro = mapToAvro(hubEvent);
        return buildHubEventAvro(hubEvent, avro);
    }

    private ScenarioConditionAvro mapToScenarioConditionAvro(ScenarioCondition scenarioCondition) {
        return ScenarioConditionAvro.newBuilder()
                .setSensorId(scenarioCondition.getSensorId())
                .setType(
                        switch (scenarioCondition.getType()) {
                            case ConditionType.MOTION -> ConditionTypeAvro.MOTION;
                            case ConditionType.LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
                            case ConditionType.SWITCH -> ConditionTypeAvro.SWITCH;
                            case ConditionType.TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
                            case ConditionType.CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
                            case ConditionType.HUMIDITY -> ConditionTypeAvro.HUMIDITY;
                        }
                )
                .setOperation(
                        switch (scenarioCondition.getOperation()) {
                            case ConditionOperation.EQUALS -> ConditionOperationAvro.EQUALS;
                            case ConditionOperation.GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
                            case ConditionOperation.LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
                        }
                )
                .setValue(scenarioCondition.getValue())
                .build();
    }

    private DeviceActionAvro mapToDeviceActionAvro(DeviceAction deviceAction) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(deviceAction.getSensorId())
                .setType(
                        switch (deviceAction.getType()) {
                            case ActionType.ACTIVATE -> ActionTypeAvro.ACTIVATE;
                            case ActionType.DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
                            case ActionType.INVERSE -> ActionTypeAvro.INVERSE;
                            case ActionType.SET_VALUE -> ActionTypeAvro.SET_VALUE;
                        }
                )
                .setValue(deviceAction.getValue())
                .build();
    }
}