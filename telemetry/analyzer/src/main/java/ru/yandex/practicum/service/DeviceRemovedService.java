package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceRemovedService implements HubEventService {
    private final SensorRepository sensorRepository;

    @Override
    public String getPayloadType() {
        return DeviceRemovedEventAvro.class.getSimpleName();
    }

    @Transactional
    @Override
    public void handle(HubEventAvro hub) {
        DeviceRemovedEventAvro deviceRemovedAvro = (DeviceRemovedEventAvro) hub.getPayload();
        log.info("Deleting a device with ID = {} with hub_id = {}", deviceRemovedAvro.getId(), hub.getHubId());
        sensorRepository.deleteByIdAndHubId(deviceRemovedAvro.getId(), hub.getHubId());
    }
}
