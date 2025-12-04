package ru.yandex.practicum.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.model.hub.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface HubEventProtoMapper {
    @Mapping(target = "deviceType", source = "type")
    @ValueMapping(target = "MOTION_SENSOR", source = "UNRECOGNIZED")
    DeviceAddedEvent mapDeviceAddedProtoToModel(DeviceAddedEventProto deviceAddedEventProto);

    DeviceRemovedEvent mapDeviceRemovedProtoToModel(DeviceRemovedEventProto deviceRemovedEventProto);

    @Mapping(target = "conditions", source = "conditionsList")
    @Mapping(target = "actions", source = "actionsList")
    ScenarioAddedEvent mapScenarioAddedProtoToModel(ScenarioAddedEventProto scenarioAddedEventProto);

    ScenarioRemovedEvent mapScenarioRemovedProtoToModel(ScenarioRemovedEventProto scenarioRemovedEventProto);

    @Mapping(target = "type", source = "type")
    @Mapping(target = "operation", source = "operation")
    @Mapping(target = "value", expression = "java(mapScenarioConditionProtoValueToModelValue(scenarioConditionProto))")
    ScenarioCondition mapScenarioConditionProtoToModel(ScenarioConditionProto scenarioConditionProto);

    @ValueMapping(target = "ACTIVATE", source = "UNRECOGNIZED")
    ActionType mapActionTypeProtoToModel(ActionTypeProto actionTypeProto);

    @ValueMapping(target = "EQUALS", source = "UNRECOGNIZED")
    ConditionOperation mapConditionOperationProtoToModel(ConditionOperationProto conditionOperationProto);

    @ValueMapping(target = "MOTION", source = "UNRECOGNIZED")
    ConditionType mapConditionTypeProtoToModel(ConditionTypeProto conditionTypeProto);

    @Named("mapScenarioConditionProtoValueToModelValue")
    default Object mapScenarioConditionProtoValueToModelValue(ScenarioConditionProto proto) {
        if (proto.hasBoolValue())
            return proto.getBoolValue();
        else if (proto.hasIntValue())
            return proto.getIntValue();
        else
            return null;
    }
}