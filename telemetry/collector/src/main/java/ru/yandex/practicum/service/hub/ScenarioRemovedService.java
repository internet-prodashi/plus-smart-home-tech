package ru.yandex.practicum.service.hub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.hub.*;

@Component
public class ScenarioRemovedService extends HubEventService<ScenarioRemovedEventAvro> {
    public ScenarioRemovedService(KafkaProducerEvent kafkaProducerEvent, @Value("${topic.hub-events}") String topicsNames) {
        super(kafkaProducerEvent, topicsNames);
    }

    public HubEventType getType() {
        return HubEventType.SCENARIO_REMOVED;
    }

    public ScenarioRemovedEventAvro mapToAvro(HubEvent hubEvent) {
        ScenarioRemovedEvent scenarioRemovedEvent = (ScenarioRemovedEvent) hubEvent;
        return ScenarioRemovedEventAvro.newBuilder()
                .setName(scenarioRemovedEvent.getName())
                .build();
    }

    protected HubEventAvro mapToAvroHubEvent(HubEvent hubEvent) {
        ScenarioRemovedEventAvro avro = mapToAvro(hubEvent);
        return buildHubEventAvro(hubEvent, avro);
    }
}