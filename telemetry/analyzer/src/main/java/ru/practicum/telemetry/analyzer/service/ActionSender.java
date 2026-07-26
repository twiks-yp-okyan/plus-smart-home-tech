package ru.practicum.telemetry.analyzer.service;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;

@Component
public class ActionSender {
    @GrpcClient("hub-router")
    private HubRouterControllerBlockingStub hubRouterClient;

    public void sendAction(DeviceActionRequest actionRequest) {
        hubRouterClient.handleDeviceAction(actionRequest);
    }
}
