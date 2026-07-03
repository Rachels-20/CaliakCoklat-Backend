package com.pbl.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.pbl.notification.config.RabbitMQConfig;
import com.pbl.notification.dto.SensorStatusEvent;
import com.pbl.notification.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SensorStatusConsumer {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.SENSOR_STATUS_QUEUE)
    public void receive(SensorStatusEvent event) {

        notificationService.processSensorStatus(event);
    }
}