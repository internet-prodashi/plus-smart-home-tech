package ru.yandex.practicum.service.hub;

import org.springframework.beans.factory.annotation.Value;
import ru.yandex.practicum.kafka.KafkaProducerEvent;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.HubEventType;

public class DeviceRemoveService extends HubEventService<DeviceRemovedEventAvro> {
    public DeviceRemoveService(KafkaProducerEvent kafkaProducerEvent, @Value("${topic.hub-events}") String topicsNames) {
        super(kafkaProducerEvent, topicsNames);
    }

    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }

    public DeviceRemovedEventAvro mapToAvro(HubEvent hubEvent) {
        DeviceRemovedEvent deviceRemovedEvent = (DeviceRemovedEvent) hubEvent;
        return DeviceRemovedEventAvro.newBuilder()
                .setId(deviceRemovedEvent.getId())
                .build();
    }

    protected HubEventAvro mapToAvroHubEvent(HubEvent hubEvent) {
        DeviceRemovedEventAvro avro = mapToAvro(hubEvent);
        return buildHubEventAvro(hubEvent, avro);
    }
}