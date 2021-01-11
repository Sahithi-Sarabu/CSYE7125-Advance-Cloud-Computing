package com.csye7125.consumer;

import com.csye7125.model.WeatherReport;
import com.csye7125.service.WatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import java.util.ArrayList;
import java.util.List;

@Component
public class MyTopicConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MyTopicConsumer.class);
    private final List<WeatherReport> messages = new ArrayList<>();

    @Autowired
    WatchService watchService;
    private final Histogram requestLatency;
    private final Counter consumeWeatherMessages;

    MyTopicConsumer(CollectorRegistry registry){
        requestLatency = Histogram.build()
                .name("requests_latency_seconds_notifier_consumer").help("Request latency in seconds.").register(registry);
        consumeWeatherMessages = Counter.build()
                .name("consume_weather").help("Counter to track messages received on weather topic in notifier")
                .register(registry);
    }

    @KafkaListener(topics = "weather", groupId = "kafka-sandbox1")
    public void listen(WeatherReport weatherReport,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.OFFSET) String offset) {
        logger.info("Consume Message on topic "+topic+" in partition "+ partition+" with offset "+ offset );
        consumeWeatherMessages.inc();
        Histogram.Timer requestTimer = requestLatency.startTimer();
        try {
            messages.add(weatherReport);
        } finally {
            requestTimer.observeDuration();
        }
        watchService.addData(weatherReport);
    }
    public List<WeatherReport> getMessages() {
        return messages;
    }
}
