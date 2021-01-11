package com.csye7125.controller;

import com.csye7125.consumer.MyTopicConsumer;
import com.csye7125.model.WeatherReport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KafkaController {

    private final MyTopicConsumer myTopicConsumer;

    public KafkaController( MyTopicConsumer myTopicConsumer) {
        this.myTopicConsumer = myTopicConsumer;
    }


    @GetMapping("/kafka/messages")
    public List<WeatherReport> getMessages() {
        return myTopicConsumer.getMessages();
    }
}
