package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.Sensor;
import ru.yandex.practicum.repository.SensorRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceAddedService implements HubEventService {
    private final SensorRepository sensorRepository;

    @Override
    public String getPayloadType() {
        return DeviceAddedEventAvro.class.getSimpleName();
    }

    @Transactional
    @Override
    public void handle(HubEventAvro hub) {
        DeviceAddedEventAvro deviceAddedAvro = (DeviceAddedEventAvro) hub.getPayload();
        log.info("Saving a new device with an ID = {}", hub.getHubId());
        sensorRepository.save(
                Sensor.builder()
                        .id(deviceAddedAvro.getId())
                        .hubId(hub.getHubId())
                        .build()
        );
    }
}
