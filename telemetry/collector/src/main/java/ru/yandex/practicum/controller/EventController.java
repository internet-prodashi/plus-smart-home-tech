package ru.yandex.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.yandex.practicum.grpc.telemetry.collector.CollectorControllerGrpc;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import com.google.protobuf.Empty;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import ru.yandex.practicum.service.hub.HubEventService;
import ru.yandex.practicum.service.sensor.SensorEventService;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
public class EventController extends CollectorControllerGrpc.CollectorControllerImplBase {
    private final Map<HubEventProto.PayloadCase, HubEventService<?>> hubEventServices;
    private final Map<SensorEventProto.PayloadCase, SensorEventService<?>> sensorEventServices;

    public EventController(Set<HubEventService<?>> hubEventService, Set<SensorEventService<?>> sensorEventService) {
        this.hubEventServices = hubEventService.stream()
                .collect(Collectors.toMap(HubEventService::getType, Function.identity()));
        this.sensorEventServices = sensorEventService.stream()
                .collect(Collectors.toMap(SensorEventService::getType, Function.identity()));
    }

    @Override
    public void collectSensorEvent(SensorEventProto sensorProto, StreamObserver<Empty> responseObserver) {
        try {
            if (sensorEventServices.containsKey(sensorProto.getPayloadCase())) {
                sensorEventServices.get(sensorProto.getPayloadCase()).handle(sensorProto);
            } else {
                log.error("Sensor handler not found for type: {}", sensorProto.getPayloadCase());
                throw new IllegalArgumentException("Sensor handler not found");
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }

    @Override
    public void collectHubEvent(HubEventProto hubProto, StreamObserver<Empty> responseObserver) {
        try {
            if (hubEventServices.containsKey(hubProto.getPayloadCase())) {
                hubEventServices.get(hubProto.getPayloadCase()).handle(hubProto);
                log.info("Hub event successfully processed");
            } else {
                log.error("Hub handler not found for type: {}", hubProto.getPayloadCase());
                throw new IllegalArgumentException("Hub handler not found");
            }
            responseObserver.onNext(Empty.getDefaultInstance());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(new StatusRuntimeException(Status.fromThrowable(e)));
        }
    }
}