package ru.yandex.practicum.service.hub;

import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.service.mapper.HubEventAvroMapper;
import ru.yandex.practicum.service.mapper.HubEventProtoMapper;

public class DeviceRemoveService extends HubEventService<DeviceRemovedEventAvro> {
    public DeviceRemoveService(
            KafkaProducerEvent kafkaProducerEvent,
            @Value("${topic.hub-events}") String topicsNames,
            HubEventProtoMapper protoMapper,
            HubEventAvroMapper avroMapper
    ) {
        super(kafkaProducerEvent, topicsNames, protoMapper, avroMapper);
    }

    public HubEventProto.PayloadCase getType() {
        return HubEventProto.PayloadCase.DEVICE_REMOVED;
    }

    @Override
    protected HubEventAvro mapHubToAvro(HubEvent hubEvent) {
        return buildHubEventAvro(
                hubEvent,
                avroMapper.mapDeviceRemoveToAvro((DeviceRemovedEvent) hubEvent)
        );
    }

    @Override
    protected HubEvent mapHubProtoToModel(HubEventProto hubProto) {
        return mapBaseHubProtoFieldsToHub(
                protoMapper.mapDeviceAddedProtoToModel(hubProto.getDeviceAdded()),
                hubProto
        );
    }
}