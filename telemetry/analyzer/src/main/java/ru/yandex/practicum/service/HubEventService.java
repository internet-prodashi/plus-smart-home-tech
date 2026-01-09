package ru.yandex.practicum.service;

import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

public interface HubEventService {
    String getPayloadType();

    void handle(HubEventAvro hub);
}