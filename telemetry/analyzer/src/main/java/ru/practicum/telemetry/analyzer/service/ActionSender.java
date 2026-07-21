package ru.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;

@Service
@RequiredArgsConstructor
public class ActionSender {
    @GrpcClient("hub-router")
    private final HubRouterControllerBlockingStub hubRouterClient;

    public void sendAction(DeviceActionRequest actionRequest) {
        hubRouterClient.handleDeviceAction(actionRequest);
    }
}
