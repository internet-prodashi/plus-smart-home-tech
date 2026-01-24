package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.repository.ScenarioActionRepository;
import ru.yandex.practicum.repository.ScenarioConditionRepository;
import ru.yandex.practicum.repository.ScenarioRepository;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScenarioRemovedService implements HubEventService {
    private final ScenarioActionRepository scenarioActionRepository;
    private final ScenarioConditionRepository scenarioConditionRepository;
    private final ScenarioRepository scenarioRepository;

    @Override
    public String getPayloadType() {
        return ScenarioRemovedEventAvro.class.getSimpleName();
    }

    @Transactional
    @Override
    public void handle(HubEventAvro hub) {
        ScenarioRemovedEventAvro scenarioRemovedEvent = (ScenarioRemovedEventAvro) hub.getPayload();
        log.info("Deleting the script with the name = {} and with the ID = {}", scenarioRemovedEvent.getName(), hub.getHubId());

        Optional<Scenario> scenarioOptional = scenarioRepository.findByHubIdAndName(hub.getHubId(), scenarioRemovedEvent.getName());
        if (scenarioOptional.isPresent()) {
            Scenario scenario = scenarioOptional.get();
            scenarioActionRepository.deleteByScenario(scenario);
            scenarioConditionRepository.deleteByScenario(scenario);
            scenarioRepository.delete(scenario);
        } else {
            log.info("The script with name = {} and ID = {} was not found", scenarioRemovedEvent.getName(), hub.getHubId());
        }
    }
}