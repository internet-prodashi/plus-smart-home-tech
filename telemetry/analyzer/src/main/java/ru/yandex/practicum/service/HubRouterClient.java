package ru.yandex.practicum.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.model.Action;
import ru.yandex.practicum.model.Scenario;
import ru.yandex.practicum.model.ScenarioAction;
import ru.yandex.practicum.model.Sensor;

import java.time.Instant;

@Slf4j
@Service
public class HubRouterClient {
    private final HubRouterControllerGrpc.HubRouterControllerBlockingStub hubRouter;

    public HubRouterClient(@GrpcClient("hub-router") HubRouterControllerGrpc.HubRouterControllerBlockingStub hub) {
        this.hubRouter = hub;
    }

    public void sendAction(ScenarioAction scenarioAction) {
        log.info("An action has been sent for the scenario ScenarioAction {}", scenarioAction);

        DeviceActionRequest actionRequest = mapToActionRequest(scenarioAction);
        log.info("Action sent for the script: Name = {} and HubId = {}",
                actionRequest.getScenarioName(),
                actionRequest.getHubId());
        hubRouter.handleDeviceAction(actionRequest);
    }

    private DeviceActionRequest mapToActionRequest(ScenarioAction scenarioAction) {
        Scenario scenario = scenarioAction.getScenario();
        Sensor sensor = scenarioAction.getSensor();
        Action action = scenarioAction.getAction();

        if (action.getType() == ActionTypeAvro.SET_VALUE && action.getValue() == null) return null;

        DeviceActionProto.Builder actionBuilder = DeviceActionProto.newBuilder()
                .setSensorId(sensor.getId())
                .setType(mapActionTypeToProto(action.getType()));

        if (action.getValue() != null) actionBuilder.setValue(action.getValue());

        return DeviceActionRequest.newBuilder()
                .setHubId(scenario.getHubId())
                .setScenarioName(scenario.getName())
                .setAction(actionBuilder.build())
                .setTimestamp(currentTimestamp())
                .build();
    }

    private ActionTypeProto mapActionTypeToProto(ActionTypeAvro actionType) {
        return switch (actionType) {
            case ACTIVATE -> ActionTypeProto.ACTIVATE;
            case DEACTIVATE -> ActionTypeProto.DEACTIVATE;
            case INVERSE -> ActionTypeProto.INVERSE;
            case SET_VALUE -> ActionTypeProto.SET_VALUE;
        };
    }

    private Timestamp currentTimestamp() {
        Instant instant = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
}