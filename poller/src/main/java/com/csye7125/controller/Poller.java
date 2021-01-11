package com.csye7125.controller;

import com.csye7125.model.Watch;
import com.csye7125.model.WatchRepository;
import com.csye7125.model.WeatherReport;
import com.csye7125.service.KafkaConsumerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
public class Poller {

    @Autowired
    WatchRepository watchRepository;
    @Autowired
    KafkaConsumerService kafkaConsumerService;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    private final Histogram requestLatency;
    private final Counter publishWeatherMessages;

    public Poller(CollectorRegistry registry){
        requestLatency = Histogram.build()
                .name("requests_latency_seconds_poller_publish").help("Request latency in seconds.").register(registry);
        publishWeatherMessages = Counter.build()
                .name("publish_weather").help("Counter to track messages received on weather topic in poller")
                .register(registry);
    }

    private static final Logger logger = LoggerFactory.getLogger(Poller.class);

    @Scheduled(fixedDelayString = "${fixedRate}000")
    public void autoUpdateState(){
        List<Watch> watchList = getWatches();
        System.out.println(watchList);
        if(watchList.size() != 0){
            for(int i = 0; i < watchList.size(); i++){
                run(watchList.get(i));
            }
        }
    }

    public List<Watch> getWatches() {
        List<Watch> watchList = watchRepository.findAll();
        return watchList;
    }

    public void run(Watch message) {
        WeatherReport weather = new WeatherReport();
        String API_KEY = "ed295f6e04f43b00efe0897fbeb3415c";
        String ZIPCODE = message.getZipcode();
        String urlString = "http://api.openweathermap.org/data/2.5/weather?zip=" + ZIPCODE + ",us&appid=" + API_KEY + "&units =imperial";
        try {
            StringBuilder result = new StringBuilder();
            URL url = null;
            url = new URL(urlString);
            URLConnection conn = null;
            conn = url.openConnection();
            BufferedReader rd = null;
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while (true) {
                if (!((line = rd.readLine()) != null)) break;
                result.append(line);
            }
            rd.close();

            Map<String, Object> respMap = jsonToMap(result.toString());
            Map<String, Object> mainMap = jsonToMap(respMap.get("main").toString());
            System.out.print(mainMap);
            weather.setUser_id(message.getUser_id());
            weather.setWatch_id(message.getWatch_id());
            weather.setWatch_created(message.getWatch_created());
            weather.setWatch_updated(message.getWatch_updated());
            weather.setZipcode(message.getZipcode());
            weather.setAlerts(message.getAlerts());
            String temp = mainMap.get("temp").toString();
            weather.setTemp(Float.parseFloat(temp));
            String temp_min = mainMap.get("temp_min").toString();
            weather.setTemp_min(Float.parseFloat(temp_min));
            String temp_max = mainMap.get("temp_max").toString();
            weather.setTemp_max(Float.parseFloat(temp_max));
            String humidity = mainMap.get("humidity").toString();
            weather.setHumidity(Float.parseFloat(humidity));
            String pressure = mainMap.get("pressure").toString();
            weather.setPressure(Float.parseFloat(pressure));
            String feels_like = mainMap.get("feels_like").toString();
            weather.setFeels_like(Float.parseFloat(feels_like));
            weather.setDeletedWatchIds(kafkaConsumerService.getDeletedWatchIds());
            logger.info("Publish Message "+weather.toString()+ "on Topic weather");
            publishWeatherMessages.inc();
            Histogram.Timer requestTimer = requestLatency.startTimer();
            try {
                kafkaTemplate.send("weather", weather);
            } finally {
                requestTimer.observeDuration();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Map<String,Object> jsonToMap(String str){
        Map<String,Object> map = new Gson().fromJson(str,new TypeToken<HashMap<String,Object>>() {}.getType());
        return map;
    }


}
