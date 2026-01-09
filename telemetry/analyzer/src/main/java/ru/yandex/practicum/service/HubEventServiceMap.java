package ru.yandex.practicum.service;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Component
public class HubEventServiceMap {
    private final Map<String, HubEventService> hubMap;

    public HubEventServiceMap(Set<HubEventService> hubSet) {
        this.hubMap = hubSet.stream()
                .collect(Collectors.toMap(
                        HubEventService::getPayloadType,
                        Function.identity()
                ));
    }
}