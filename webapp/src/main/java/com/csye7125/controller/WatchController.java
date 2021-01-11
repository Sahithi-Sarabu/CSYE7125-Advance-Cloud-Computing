package com.csye7125.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csye7125.model.Watch;
import com.csye7125.service.WatchService;
import io.prometheus.client.CollectorRegistry;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.prometheus.client.*;



@RestController
public class WatchController {
    private static final Logger logger = LoggerFactory.getLogger(WatchController.class);
    @Autowired
    WatchService watchService;

    final Counter createWatchRequests;
    final Counter getWatchByIdRequests;
    final Counter updateWatchRequests;
    final Counter deleteWatchRequests;
    final Counter getAllWatchRequests;


    public WatchController(CollectorRegistry registry){
        createWatchRequests = Counter.build().name("create_watch").help("Counter to track watch creation").register(registry);
        getWatchByIdRequests = Counter.build().name("get_watch_by_ID").help("Counter to track get watch requests by ID").register(registry);
        updateWatchRequests = Counter.build().name("update_watch").help("Counter to track update watch requests").register(registry);
        deleteWatchRequests = Counter.build().name("delete_watch").help("Counter to track delete watch requests").register(registry);
        getAllWatchRequests = Counter.build().name("get_all_watches").help("Counter to track get all watches requests").register(registry);

    }

    @PostMapping("v1/watch")
    public ResponseEntity<JSONObject> createWatch(@RequestBody Watch watch){
        createWatchRequests.inc();
        logger.info("Create Watch");
        return watchService.createWatch(watch);
    }

    @GetMapping("v1/watch/{watch_id}")
    public ResponseEntity<JSONObject> getWatchByID(@PathVariable String watch_id){
        getWatchByIdRequests.inc();
        logger.info("Get Watch "+ watch_id);
        return watchService.getWatchByID(watch_id);
    }

    @PutMapping("v1/watch/{watch_id}")
    public ResponseEntity<JSONObject> updateWatch(@PathVariable String watch_id,@RequestBody Watch watch) {
        updateWatchRequests.inc();
        logger.info("Update Watch "+ watch_id);
        return watchService.updateWatch(watch_id,watch);
    }

    @DeleteMapping("v1/watch/{watch_id}")
    public ResponseEntity<JSONObject> deleteWatchByID(@PathVariable String watch_id){
        deleteWatchRequests.inc();
        logger.info("Delete Watch "+ watch_id);
        return watchService.deleteWatchByID(watch_id);
    }

    @GetMapping("v1/watches")
    public ResponseEntity<JSONArray> getWatches() {
        getAllWatchRequests.inc();
        logger.info("Get all Watches");
        return watchService.getWatches();
    }
}
