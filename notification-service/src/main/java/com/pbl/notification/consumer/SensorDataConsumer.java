package com.pbl.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.pbl.notification.config.RabbitMQConfig;
import com.pbl.notification.dto.SensorDataEvent;
import com.pbl.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SensorDataConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.SENSOR_QUEUE)
    public void receive(SensorDataEvent event) {
        notificationService.process(event);
    }
}