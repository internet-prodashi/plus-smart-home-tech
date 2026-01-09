package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.model.*;
import ru.yandex.practicum.repository.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioAddedService implements HubEventService {
    private final ScenarioRepository scenarioRepository;
    private final ActionRepository actionRepository;
    private final ConditionRepository conditionRepository;
    private final SensorRepository sensorRepository;
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;

    @Override
    public String getPayloadType() {
        return ScenarioAddedEventAvro.class.getSimpleName();
    }

    @Transactional
    @Override
    public void handle(HubEventAvro hub) {
        ScenarioAddedEventAvro avro = (ScenarioAddedEventAvro) hub.getPayload();

        Scenario scenario = scenarioRepository.findByHubIdAndName(hub.getHubId(), avro.getName())
                .orElseGet(() -> scenarioRepository.save(
                        Scenario.builder()
                                .hubId(hub.getHubId())
                                .name(avro.getName())
                                .build()));

        scenarioActionRepository.deleteByScenario(scenario);
        scenarioConditionRepository.deleteByScenario(scenario);

        avro.getConditions().forEach(SCA -> {
            Sensor sensor = sensorRepository.findById(SCA.getSensorId())
                    .orElseGet(() -> sensorRepository.save(
                            Sensor.builder()
                                    .id(SCA.getSensorId())
                                    .hubId(hub.getHubId())
                                    .build()));
            Condition condition = conditionRepository.save(
                    Condition.builder()
                            .type(SCA.getType())
                            .operation(SCA.getOperation())
                            .value(asInteger(SCA.getValue()))
                            .build());
            scenarioConditionRepository.save(
                    ScenarioCondition.builder()
                            .scenario(scenario)
                            .sensor(sensor)
                            .condition(condition)
                            .id(new ScenarioConditionId(
                                    scenario.getId(),
                                    sensor.getId(),
                                    condition.getId()))
                            .build());
        });

        avro.getActions().forEach(DAA -> {
            Sensor sensor = sensorRepository.findById(DAA.getSensorId())
                    .orElseGet(() -> sensorRepository.save(
                            Sensor.builder()
                                    .id(DAA.getSensorId())
                                    .hubId(hub.getHubId())
                                    .build()));
            Action action = actionRepository.save(
                    Action.builder()
                            .type(DAA.getType())
                            .value(DAA.getValue())
                            .build());
            scenarioActionRepository.save(
                    ScenarioAction.builder()
                            .scenario(scenario)
                            .sensor(sensor)
                            .action(action)
                            .id(new ScenarioActionId(
                                    scenario.getId(),
                                    sensor.getId(),
                                    action.getId()))
                            .build());
        });
    }

    private Integer asInteger(Object value) {
        return value instanceof Integer
                ? (Integer) value
                : ((Boolean) value ? 1 : 0);
    }
}