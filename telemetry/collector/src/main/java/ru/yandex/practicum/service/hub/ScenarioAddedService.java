package ru.yandex.practicum.service.hub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;
import ru.yandex.practicum.service.mapper.HubEventAvroMapper;
import ru.yandex.practicum.service.mapper.HubEventProtoMapper;

@Component
public class ScenarioAddedService extends HubEventService<ScenarioAddedEventAvro> {
    public ScenarioAddedService(
            KafkaProducerEvent kafkaProducerEvent,
            @Value("${topic.hub-events}") String topicsNames,
            HubEventProtoMapper protoMapper,
            HubEventAvroMapper avroMapper
    ) {
        super(kafkaProducerEvent, topicsNames, protoMapper, avroMapper);
    }

    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }

    @Override
    protected HubEventAvro mapHubToAvro(HubEvent hubEvent) {
        return buildHubEventAvro(
                hubEvent,
                avroMapper.mapScenarioAddedToAvro((ScenarioAddedEvent) hubEvent)
        );
    }

    @Override
    protected HubEvent mapHubProtoToModel(HubEventProto hubProto) {
        return mapBaseHubProtoFieldsToHub(
                protoMapper.mapScenarioAddedProtoToModel(hubProto.getScenarioAdded()),
                hubProto
        );
    }
}