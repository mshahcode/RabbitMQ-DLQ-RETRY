package org.example.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final String EXCHANGE = "_Exchange";
    private static final String KEY = "_Key";

    @Value("${spring.application.name}")
    private String appName;

    private final String simpleMessageQ;
    private final String simpleMessageQDlq;
    private final String simpleMessageQDlqExchange;
    private final String simpleMessageQKey;
    private final String simpleMessageQExchange;
    private final String simpleMessageQDlqKey;


    public RabbitMQConfig(@Value("${rabbitmq.queue.test}") String simpleMessageQ,
                          @Value("${rabbitmq.queue.testDlq}") String simpleMessageQDlq) {
        this.simpleMessageQ = simpleMessageQ;
        this.simpleMessageQKey = simpleMessageQ + KEY;
        this.simpleMessageQExchange = simpleMessageQ + EXCHANGE;
        this.simpleMessageQDlq = simpleMessageQDlq;
        this.simpleMessageQDlqKey = simpleMessageQDlq + KEY;
        this.simpleMessageQDlqExchange = simpleMessageQDlq + EXCHANGE;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setConsumerTagStrategy(q -> appName);
        return factory;
    }

    @Bean
    public Queue simpleMessageQueue() {
        return QueueBuilder.durable(this.simpleMessageQ)
                .withArgument("x-dead-letter-exchange", this.simpleMessageQDlqExchange)
                .withArgument("x-dead-letter-routing-key", this.simpleMessageQDlqKey)
                .build();
    }

    @Bean
    public DirectExchange simpleMessageExchange() {
        return new DirectExchange(this.simpleMessageQExchange);
    }

    @Bean
    public Binding binding() {
        return BindingBuilder
                .bind(simpleMessageQueue())
                .to(simpleMessageExchange())
                .with(this.simpleMessageQKey);
    }

    @Bean
    public Queue simpleMessageQueueDlq() {
        return QueueBuilder.durable(this.simpleMessageQDlq).build();
    }

    @Bean
    public DirectExchange simpleMessageExchangeDlq() {
        return new DirectExchange(this.simpleMessageQDlqExchange);
    }

    @Bean
    public Binding dlQbinding() {
        return BindingBuilder
                .bind(simpleMessageQueueDlq())
                .to(simpleMessageExchangeDlq())
                .with(this.simpleMessageQDlqKey);
    }
}