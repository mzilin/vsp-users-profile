package com.mariuszilinskas.vsp.userprofileservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange}")
    private String exchange;

    @Value("${rabbitmq.queues.profile-setup}")
    private String profileSetupQueue;

    @Value("${rabbitmq.routing-keys.profile-setup}")
    private String profileSetupRoutingKey;

    @Value("${rabbitmq.queues.delete-user-data}")
    private String deleteUserDataQueue;

    @Value("${rabbitmq.routing-keys.delete-user-data}")
    private String deleteUserDataRoutingKey;

    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue profileSetupQueue() {
        return new Queue(profileSetupQueue, true);
    }

    @Bean
    public Binding profileSetupBinding() {
        return BindingBuilder.bind(profileSetupQueue())
                .to(exchange())
                .with(profileSetupRoutingKey);
    }

    @Bean
    public Queue deleteUserDataQueue() {
        return new Queue(deleteUserDataQueue, true);
    }

    @Bean
    public Binding deleteUserDataBinding() {
        return BindingBuilder.bind(deleteUserDataQueue())
                .to(exchange())
                .with(deleteUserDataRoutingKey);
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jacksonConverter());
        return rabbitTemplate;
    }

    @Bean
    public MessageConverter jacksonConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
