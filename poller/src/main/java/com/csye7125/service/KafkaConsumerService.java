package com.csye7125.service;

import com.csye7125.model.*;
import io.prometheus.client.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;

import java.util.ArrayList;
import java.util.List;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    WatchRepository watchRepository;
    private final Histogram requestLatency;
    private final Counter consumeWatchMessages;

    public KafkaConsumerService(CollectorRegistry registry){
        requestLatency = Histogram.build()
                .name("requests_latency_seconds_poller_consumer").help("Request latency in seconds.").register(registry);
        consumeWatchMessages = Counter.build()
                .name("consume_watch").help("Counter to track messages received on watch topic in poller")
                .register(registry);

    }
    private final List<Watch> messages = new ArrayList<>();
    private final List<String> deletedWatchIds = new ArrayList<>();

    @KafkaListener(topics = "watch", groupId = "kafka-sandbox")
    public void listen(Message message,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.OFFSET) String offset) {
        consumeWatchMessages.inc();
        logger.info("Consume Message on topic "+topic+" in partition "+ partition+" with offset "+ offset );
        Watch watch = new Watch();
        watch.setWatch_id(message.getWatch_id());
        watch.setUser_id(message.getUser_id());
        watch.setWatch_created(message.getWatch_created());
        watch.setWatch_updated(message.getWatch_updated());
        watch.setZipcode(message.getZipcode());
        watch.setAlerts(message.getAlerts());
        if(message.getStatus().equals("inactive")){
            watchRepository.delete(watch);
            deletedWatchIds.add(message.getWatch_id());
        }else{
            Histogram.Timer requestTimer = requestLatency.startTimer();
            try {
                messages.add(watch);
            } finally {
                requestTimer.observeDuration();
            }
            watchRepository.save(watch);
        }
    }

    public List<Watch> getMessages() {
        return messages;
    }

    public List<String> getDeletedWatchIds() {
        return deletedWatchIds;
    }

}
