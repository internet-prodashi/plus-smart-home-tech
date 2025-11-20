package ru.yandex.practicum.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.HubEventType;
import ru.yandex.practicum.model.sensor.SensorEvent;
import ru.yandex.practicum.model.sensor.SensorEventType;
import ru.yandex.practicum.service.hub.HubEventService;
import ru.yandex.practicum.service.sensor.SensorEventService;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/events")
public class EventController {
    private final Map<HubEventType, HubEventService<?>> hubEventServices;
    private final Map<SensorEventType, SensorEventService<?>> sensorEventServices;

    public EventController(Set<HubEventService<?>> hubEventService, Set<SensorEventService<?>> sensorEventService) {
        this.hubEventServices = hubEventService.stream()
                .collect(Collectors.toMap(HubEventService::getType, Function.identity()));
        this.sensorEventServices = sensorEventService.stream()
                .collect(Collectors.toMap(SensorEventService::getType, Function.identity()));
    }

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        if (sensorEventServices.containsKey(sensorEvent.getType())) {
            sensorEventServices.get(sensorEvent.getType()).handle(sensorEvent);
            log.info("Sensor event successfully processed");
        } else {
            log.error("Sensor handler not found for type: {}", sensorEvent.getType());
            throw new IllegalArgumentException("Sensor handler not found");
        }
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        if (hubEventServices.containsKey(hubEvent.getType())) {
            hubEventServices.get(hubEvent.getType()).handle(hubEvent);
            log.info("Hub event successfully processed");
        } else {
            log.error("Hub handler not found for type: {}", hubEvent.getType());
            throw new IllegalArgumentException("Hub handler not found");
        }
    }
}
