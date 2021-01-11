package com.csye7125.controller;

import com.csye7125.model.Watch;
import com.csye7125.service.KafkaConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class KafkaController {
    @Autowired
    KafkaConsumerService kafkaConsumerService;

    @GetMapping("/kafka/consume")
    public List<Watch> getMessages() {
        return kafkaConsumerService.getMessages();
    }

}
