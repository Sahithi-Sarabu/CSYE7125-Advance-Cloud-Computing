package com.csye7125.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csye7125.controller.WatchController;
import com.csye7125.model.*;
import com.csye7125.service.WatchService;
import com.csye7125.model.Message;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Histogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service("watchService")
public class WatchServiceImpl implements WatchService {
    private final Histogram requestLatency;
    private final Histogram requestLatency1;
    private final Histogram requestLatency2;
    private final Histogram requestLatency3;

    public WatchServiceImpl(CollectorRegistry registry){
        requestLatency = Histogram.build()
                .name("requests_latency_seconds_watch").help("Request latency in seconds.").register(registry);
        requestLatency1 = Histogram.build()
                .name("requests_latency_seconds_kafka").help("Request latency in seconds.").register(registry);
        requestLatency2 = Histogram.build()
                .name("requests_latency_seconds_alert").help("Request latency in seconds.").register(registry);
        requestLatency3 = Histogram.build()
                .name("requests_latency_seconds_user_watch").help("Request latency in seconds.").register(registry);
    }

    @Autowired
    WatchRepository watchRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceImpl userService;

    @Autowired
    AlertRepository alertRepository;

    @Autowired
    private KafkaTemplate kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(WatchController.class);

    ResponseEntity BadRequest = new ResponseEntity<>(JSONObject.parseObject(JSON.toJSONString(null)), HttpStatus.BAD_REQUEST);
    ResponseEntity NoContent = new ResponseEntity<>(JSONObject.parseObject(JSON.toJSONString(null)), HttpStatus.NO_CONTENT);
    ResponseEntity NotFound = new ResponseEntity<>(JSONObject.parseObject(JSON.toJSONString(null)), HttpStatus.NOT_FOUND);
    ResponseEntity Unauthorized = new ResponseEntity<>(JSONObject.parseObject(JSON.toJSONString(null)), HttpStatus.UNAUTHORIZED);

    @Override
    public ResponseEntity<JSONObject> createWatch(Watch watch) {
        String currentUserID = UserServiceImpl.getCurrentUserName();
        User user = userRepository.findByUsername(currentUserID);
        if(watch.getWatch_id()!=null||watch.getUser_id()!=null||watch.getWatch_created()!=null||watch.getWatch_updated()!=null){
            return BadRequest;
        }
        watch.setUser_id(user.getId());
        watch.setWatch_created(String.valueOf(new Date()));
        watch.setWatch_updated(String.valueOf(new Date()));
        List<Alert> alerts = watch.getAlerts();
        for (Alert alert:alerts
             ) {
            alert.setAlert_created(String.valueOf(new Date()));
            alert.setAlert_updated(String.valueOf(new Date()));
            Histogram.Timer requestTimer1 = requestLatency2.startTimer();
            try {
                alertRepository.save(alert);
            } finally {
                requestTimer1.observeDuration();
            }
        }
        if(watch.getAlerts()==null){
            return BadRequest;
        }
        Histogram.Timer requestTimer2 = requestLatency.startTimer();
        try {
            watchRepository.save(watch);
            } catch (Exception e) {
            return BadRequest;
        }finally {
            requestTimer2.observeDuration();
        }
        Message message = message("active",watch);
        Histogram.Timer requestTimer3 = requestLatency1.startTimer();
        try {
            kafkaTemplate.send("watch",message);
        } finally {
            requestTimer3.observeDuration();
        }
        logger.info("Publish Message "+message.toString()+ "on Topic watch");
        return new ResponseEntity<>(JSONObject.parseObject(JSON.toJSONString(watch)), HttpStatus.CREATED);

    }

    @Override
    public ResponseEntity<JSONObject> getWatchByID(String watch_id) {
        String currentUserID = UserServiceImpl.getCurrentUserName();
        User user;
        Histogram.Timer requestTimer = requestLatency3.startTimer();
        try {
            user = userRepository.findByUsername(currentUserID);
        } finally {
            requestTimer.observeDuration();
        }
        Watch watch;
        requestTimer = requestLatency.startTimer();
        try {
            watch = watchRepository.findWatchByWatchIdAndUserId(watch_id,user.getId());
        } finally {
            requestTimer.observeDuration();
        }
        if(watch==null) return NotFound;
        return new ResponseEntity<>(JSONObject.parseObject(JSON.toJSONString(watch)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<JSONObject> updateWatch(String watch_id, Watch watch) {
        String currentUserID = UserServiceImpl.getCurrentUserName();
        User user;
        Watch watchOld;
        Histogram.Timer requestTimer = requestLatency3.startTimer();
        try {
            user = userRepository.findByUsername(currentUserID);
        } finally {
            requestTimer.observeDuration();
        }
        requestTimer = requestLatency.startTimer();
        try{
            watchOld = watchRepository.findWatchByWatchId(watch_id);
        } finally {
            requestTimer.observeDuration();
        }
        if(watchOld==null) return NotFound;
        if(!user.getId().equals(watchOld.getUser_id())) return Unauthorized;
        watchOld.setZipcode(watch.getZipcode());
        watchOld.setWatch_updated(String.valueOf(new Date()));
        Iterator<Alert> alertIterator = watchOld.getAlerts().iterator();
        while(alertIterator.hasNext()){
            Alert alert = alertIterator.next();
            alertRepository.deleteById(alert.getAlert_id());
        }
        List<Alert> alerts = watch.getAlerts();
        for (Alert alert:alerts
        ) {
            alert.setAlert_created(String.valueOf(new Date()));
            alert.setAlert_updated(String.valueOf(new Date()));
            alertRepository.save(alert);
        }
        watchOld.setAlerts(alerts);
        watchRepository.save(watchOld);
        Message message = message("active",watchOld);
        requestTimer = requestLatency1.startTimer();
        try{
            kafkaTemplate.send("watch",message);
        } finally {
            requestTimer.observeDuration();
        }
        logger.info("Publish Message "+message.toString()+ "on Topic watch");
        return NoContent;
    }

    @Override
    public ResponseEntity<JSONObject> deleteWatchByID(String watch_id) {
        String currentUserID = userService.getCurrentUserID();
        Watch watchOld = watchRepository.findWatchByWatchId(watch_id);
        if(watchOld==null) return NotFound;
        if(!currentUserID.equals(watchOld.getUser_id())) return Unauthorized;
        Iterator<Alert> alertIterator = watchOld.getAlerts().iterator();
        while(alertIterator.hasNext()){
            Alert alert = alertIterator.next();
            alertRepository.delete(alert);
        }
        Histogram.Timer requestTimer = requestLatency.startTimer();
        try{
            watchRepository.delete(watchOld);
        }finally {
            requestTimer.observeDuration();
        }
        requestTimer = requestLatency1.startTimer();
        Message message = message("inactive",watchOld);
        try{
            kafkaTemplate.send("watch",message);
        }finally {
            requestTimer.observeDuration();
        }
        logger.info("Publish Message "+message.toString()+ "on Topic watch");
        return NoContent;
    }

    @Override
    public ResponseEntity<JSONArray> getWatches() {
        String currentUserID = userService.getCurrentUserID();
        Histogram.Timer requestTimer = requestLatency.startTimer();
        List<Watch> watchList;
        try{
            watchList= watchRepository.findAllByUserId(currentUserID);
        } finally {
            requestTimer.observeDuration();
        }
        return new ResponseEntity<>(JSONArray.parseArray(JSON.toJSONString(watchList)), HttpStatus.OK);

    }

    public Message message(String status, Watch watch){
        Message message = new Message(status,watch.getWatch_id(),watch.getUser_id(),
                watch.getWatch_created(),watch.getWatch_updated(),watch.getZipcode(),watch.getAlerts());
        return message;

    }


}
