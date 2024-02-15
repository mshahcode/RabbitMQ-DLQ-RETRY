package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.example.service.RabbitMqProducer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mq")
@RequiredArgsConstructor
public class RabbitMqController {
    private final RabbitMqProducer producer;

    @GetMapping("/publish")
    public void sendtoQueue() throws JsonProcessingException {
        producer.sendMessage();
    }
}
