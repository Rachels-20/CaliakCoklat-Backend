package com.pbl.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.pbl.notification.dto.DeviceOfflineEvent;
import com.pbl.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeviceOfflineConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "device.offline.queue")
    public void receive(DeviceOfflineEvent event) {

        System.out.printf(
                "[RabbitMQ] Device Offline | %s | User %d%n",
                event.getKodePerangkat(),
                event.getUserId());

        notificationService.handleDeviceOffline(event);
    }

}