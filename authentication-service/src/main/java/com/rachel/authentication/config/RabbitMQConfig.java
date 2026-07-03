package com.rachel.authentication.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "all.events";

    public static final String RESET_PASSWORD_QUEUE = "reset.password.queue";

    public static final String RESET_PASSWORD_ROUTING_KEY = "reset.password";

    @Bean
    Queue resetPasswordQueue() {
        return new Queue(
                RESET_PASSWORD_QUEUE);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(
                EXCHANGE);
    }

    @Bean
    Binding resetPasswordBinding() {
        return BindingBuilder
                .bind(resetPasswordQueue())
                .to(exchange())
                .with(RESET_PASSWORD_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}