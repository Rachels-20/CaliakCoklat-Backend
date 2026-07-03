package com.pbl.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchange utama
    public static final String EXCHANGE = "all.events";

    // Sensor
    public static final String SENSOR_QUEUE = "notification.queue";
    public static final String SENSOR_ROUTING_KEY = "sensor.data.saved";

    // Reset Password
    public static final String RESET_PASSWORD_QUEUE = "reset.password.queue";
    public static final String RESET_PASSWORD_ROUTING_KEY = "reset.password";

    public static final String SENSOR_STATUS_QUEUE = "notification.sensor.status.queue";
    public static final String SENSOR_STATUS_ROUTING_KEY = "sensor.status.changed";

    public static final String DEVICE_ONLINE_QUEUE = "device.online.queue";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    // =========================
    // SENSOR
    // =========================

    @Bean
    public Queue sensorQueue() {
        return new Queue(SENSOR_QUEUE, true);
    }

    @Bean
    public Binding sensorBinding(
            TopicExchange exchange) {

        return BindingBuilder
                .bind(sensorQueue())
                .to(exchange)
                .with(SENSOR_ROUTING_KEY);
    }

    // =========================
    // RESET PASSWORD
    // =========================

    @Bean
    public Queue resetPasswordQueue() {
        return new Queue(
                RESET_PASSWORD_QUEUE,
                true);
    }

    @Bean
    public Binding resetPasswordBinding(
            TopicExchange exchange) {

        return BindingBuilder
                .bind(resetPasswordQueue())
                .to(exchange)
                .with(RESET_PASSWORD_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Binding sensorStatusBinding(
            TopicExchange exchange) {

        return BindingBuilder
                .bind(sensorStatusQueue())
                .to(exchange)
                .with(SENSOR_STATUS_ROUTING_KEY);
    }

    @Bean
    public Queue sensorStatusQueue() {
        return new Queue(SENSOR_STATUS_QUEUE, true);
    }

    @Bean
    public Queue deviceOfflineQueue() {
        return new Queue("device.offline.queue");
    }

    @Bean
    public Binding deviceOfflineBinding(
            Queue deviceOfflineQueue,
            TopicExchange exchange) {

        return BindingBuilder
                .bind(deviceOfflineQueue)
                .to(exchange)
                .with("device.offline");
    }

    @Bean
    public Queue deviceOnlineQueue() {
        return new Queue(DEVICE_ONLINE_QUEUE, true);
    }

    @Bean
    public Binding deviceOnlineBinding(
            TopicExchange exchange) {

        return BindingBuilder
                .bind(deviceOnlineQueue())
                .to(exchange)
                .with("device.online");
    }
}