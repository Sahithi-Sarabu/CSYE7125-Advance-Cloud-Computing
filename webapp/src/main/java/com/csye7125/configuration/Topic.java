package com.csye7125.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class Topic {

    @Bean
    public NewTopic watch(){
        return TopicBuilder.name("watch").partitions(3).replicas(2).build();
    }

    @Bean
    public NewTopic weather(){
        return TopicBuilder.name("weather").partitions(3).replicas(2).build();
    }
}
