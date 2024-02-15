package org.example.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.dto.EmailDTO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQConsumer {

    private final ObjectMapper objectMapper;
    private final AmqpTemplate amqpTemplate;

    public void ex() {
        throw new RuntimeException("Registration Failed");
    }

    @RabbitListener(queues = {"${rabbitmq.queue.test}"})
    public void consume(@Payload String message) {
        EmailDTO emailDTO = convertToObject(message);
        log.info("Email: {}", emailDTO);
        ex();
    }

    private EmailDTO convertToObject(String json) {

        try {
            return objectMapper.readValue(json, EmailDTO.class);
        } catch (JsonProcessingException e) {
            log.error("ActionLog.convertToObject error. {}", e);
            throw new RuntimeException();
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.testDlq}")
    public void reprocessDlqMessage(String message) throws InterruptedException {
        Thread.sleep(5000);
        amqpTemplate.convertAndSend(
                "simpleMessageQueue_Exchange",
                "simpleMessageQueue_Key",
                message);
    }
}
