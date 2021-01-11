package com.csye7125.configuration;


import com.csye7125.model.WeatherReport;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ConsumerConfiguration {

    private static final String KAFKA_BROKER = "kafka:9092";
    private static final String GROUP_ID = "kafka-sandbox1";
    @Bean
    public ConsumerFactory<String, WeatherReport> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigurations(), new StringDeserializer(), new JsonDeserializer<>(WeatherReport.class));
    }

    @Bean
    public Map<String, Object> consumerConfigurations() {
        Map<String, Object> configurations = new HashMap<>();
        configurations.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_BROKER);
        configurations.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configurations.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configurations.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return configurations;
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, WeatherReport> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, WeatherReport> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
