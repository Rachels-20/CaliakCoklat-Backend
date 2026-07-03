package com.pbl.notification.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import com.pbl.notification.config.RabbitMQConfig;
import com.pbl.notification.dto.ResetPasswordEvent;
import com.pbl.notification.service.EmailService;

@Component
@RequiredArgsConstructor
public class ResetPasswordConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.RESET_PASSWORD_QUEUE)
    public void consume(
            ResetPasswordEvent event) {

        emailService.sendOtp(
                event.getEmail(),
                event.getCode());
    }
}