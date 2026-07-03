package com.pbl.phtanah.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "all.events";
    public static final String ROUTING_KEY = "sensor.data.saved";
    public static final String QUEUE = "notification.queue";
    public static final String STATUS_ROUTING_KEY = "sensor.status.changed";

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}