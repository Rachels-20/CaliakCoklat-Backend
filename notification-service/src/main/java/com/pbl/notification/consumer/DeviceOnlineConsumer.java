package com.pbl.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.pbl.notification.dto.DeviceOnlineEvent;
import com.pbl.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeviceOnlineConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = "device.online.queue")
    public void receive(DeviceOnlineEvent event) {

        System.out.printf(
                "[RabbitMQ] Device Online | %s | User %d%n",
                event.getKodePerangkat(),
                event.getUserId());

        notificationService.handleDeviceOnline(event);
    }

}