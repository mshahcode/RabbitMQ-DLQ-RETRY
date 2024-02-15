package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.model.dto.EmailDTO;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class RabbitMqProducer {
    @Qualifier("rabbitTemplate")
    private final AmqpTemplate amqpTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage() throws JsonProcessingException {
        var emailDto = new EmailDTO("Mika", BigDecimal.TEN);
        amqpTemplate.convertAndSend(
                "simpleMessageQueue_Exchange",
                "simpleMessageQueue_Key",
                objectMapper.writeValueAsString(emailDto));
    }
}
