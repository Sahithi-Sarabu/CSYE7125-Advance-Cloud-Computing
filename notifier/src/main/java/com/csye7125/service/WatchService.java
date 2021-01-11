package com.csye7125.service;

import com.csye7125.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class WatchService {

    @Autowired
    WatchRepository watchRepository;

    @Autowired
    AlertRepository alertRepository;

    public String placeHolderDate = "Thu Jan 1 00:00:00 PDT 1971";
    public void addData(WeatherReport weatherReport) {
        List<String> deletedIds = weatherReport.getDeletedWatchIds();
        List<Watch> toBeDeleted = watchRepository.findAllByWatch_id(deletedIds);
        for(Watch watch : toBeDeleted){
            alertRepository.deleteAll(watch.getAlerts());
            watchRepository.delete(watch);
        }

        Watch oldWatch = watchRepository.findByWatchId(weatherReport.getWatch_id());
        if(oldWatch == null){
            oldWatch = new Watch();
            oldWatch.setWatch_id(weatherReport.getWatch_id());
            oldWatch.setWatch_created(weatherReport.getWatch_created());
            oldWatch.setUser_id(weatherReport.getUser_id());
            updateWatch(oldWatch, weatherReport);
        }else if(!oldWatch.getWatch_updated().equals(weatherReport.getWatch_updated())){
            updateWatch(oldWatch, weatherReport);
        }

        processAlerts(weatherReport);
    }

    private void updateWatch(Watch oldWatch, WeatherReport weatherReport){
        oldWatch.setZipcode(weatherReport.getZipcode());
        oldWatch.setWatch_updated(weatherReport.getWatch_updated());

        List<Alert> oldAlerts = alertRepository.findAllByWatch_id(weatherReport.getWatch_id());
        alertRepository.deleteAll(oldAlerts);
        watchRepository.save(oldWatch);
        List<Alert> alertList = weatherReport.getAlerts();
        for (Alert alert : alertList) {
            alert.setAlert_sent_time(placeHolderDate);
            alert.setAlert_status(null);
            alert.setWatch(oldWatch);
        }
        alertRepository.saveAll(alertList);
        oldWatch.setAlerts(alertList);
    }

    private void processAlerts(WeatherReport weatherReport) {
        List<Watch> watchList = watchRepository.findAllByZipcode(weatherReport.getZipcode());

        List<String> watchIdList = new ArrayList<>();
        for (Watch watch : watchList) {
            watchIdList.add(watch.getWatch_id());
        }
        HashMap<String, Alert> lastSentAlertForUsers = getLastSentAlertForUsers(watchList);
        List<Alert> alertMatchList = alertRepository.findAllByWatch(watchIdList);
        List<Alert> modifiedAlerts = new ArrayList<>();
        for(Alert alert : alertMatchList){
            Alert tempAlert = evaluateAlert(alert, weatherReport, lastSentAlertForUsers);
            modifiedAlerts.add(tempAlert);
        }
        alertRepository.saveAll(modifiedAlerts);
    }

    private HashMap<String, Alert> getLastSentAlertForUsers(List<Watch> watchList){
        HashMap<String, Alert> map = new HashMap<>();

        for(Watch watch : watchList){
            String userId = watch.getUser_id();
            Alert alert = getLastAlert(watch.getAlerts());
            if(!map.containsKey(userId)){
                map.put(userId, alert);
            }
            else{
                if (alert != null && map.get(userId) == null){
                    map.put(userId, alert);
                }
                else if(alert != null && map.get(userId) != null &&
                        alert.getAlert_sent_time().compareTo(map.get(userId).getAlert_sent_time()) > 0) {
                        map.put(userId, alert);
                }
            }
        }

        for(String key : map.keySet()){
            System.out.println("User Id:" + key);
            if(map.get(key) == null)
                System.out.println("Alert is null");
            else
                System.out.println("Alert:" + map.get(key));
        }

        return map;
    }

    private Alert getLastAlert(List<Alert> alerts) {
        Date date = parseDate(placeHolderDate);
        Alert lastSentAlert = null;
        for(Alert alert : alerts){
            Date tempDate = parseDate(alert.getAlert_sent_time());
            if (tempDate.compareTo(date) > 0) {
                date = tempDate;
                lastSentAlert = alert;
            }
        }
        return lastSentAlert;
    }

    private Alert evaluateAlert(Alert alert, WeatherReport weatherReport, HashMap<String, Alert> lastAlertedTimeForUsers){
        boolean isTriggered = false;

        switch (alert.getField_type()){
            case "temp":
                isTriggered = performOperation(alert.getOperator(), alert.getNumber(), weatherReport.getTemp());
                break;

            case "feels_like":
                isTriggered = performOperation(alert.getOperator(), alert.getNumber(), weatherReport.getFeels_like());
                break;

            case "temp_min":
                isTriggered = performOperation(alert.getOperator(), alert.getNumber(), weatherReport.getTemp_min());
                break;

            case "temp_max":
                isTriggered = performOperation(alert.getOperator(), alert.getNumber(), weatherReport.getTemp_max());
                break;

            case "pressure":
                isTriggered = performOperation(alert.getOperator(), alert.getNumber(), weatherReport.getPressure());
                break;

            case "humidity":
                isTriggered = performOperation(alert.getOperator(), alert.getNumber(), weatherReport.getHumidity());
                break;
        }

        if(!isTriggered){
            alert.setAlert_status(null);
            return alert;
        }

        String alertsUserId = alert.getWatch().getUser_id();
        Alert usersLastSentAlert = lastAlertedTimeForUsers.get(alertsUserId);
        Date usersLastAlertTime = usersLastSentAlert == null ? parseDate(placeHolderDate) : parseDate(usersLastSentAlert.getAlert_sent_time());
        long timeDifference = new Date().getTime() - usersLastAlertTime.getTime();

        System.out.println("Processing alert:" + alert.getAlert_id());
        System.out.println("User Id of alert:" + alertsUserId);
        System.out.println("Current date time:" + new Date().getTime());
        System.out.println("Date from usersLastAlertTime:" + String.valueOf(lastAlertedTimeForUsers.get(weatherReport.getUser_id())));
        System.out.println("Time difference:" + timeDifference);
        if(timeDifference >= 3600000){
            Date newDate = new Date();
            alert.setAlert_status("ALERT_SENT");
            alert.setAlert_sent_time(String.valueOf(newDate));
            lastAlertedTimeForUsers.put(alertsUserId, alert);
        } else if (usersLastSentAlert != null && alert.getAlert_id().equals(usersLastSentAlert.getAlert_id())) {
            alert.setAlert_status("ALERT_IGNORED_DUPLICATE");
        } else {
            alert.setAlert_status("ALERT_IGNORED_THRESHOLD_REACHED");
        }
        return alert;
    }

    private boolean performOperation(String operator, float thresholdValue, float reportedValue){
        switch (operator){
            case "gt":
                return reportedValue > thresholdValue;

            case "gte":
                return reportedValue >= thresholdValue;

            case "eq":
                return reportedValue == thresholdValue;

            case "lt":
                return reportedValue < thresholdValue;

            case "lte":
                return reportedValue <= thresholdValue;
        }
        return false;
    }

    private Date parseDate(String date){
        try{
            return new SimpleDateFormat("E MMM dd HH:mm:ss z yyyy").parse(date);
        }
        catch(ParseException ex){
            System.out.println("Failed to parse date:" + ex.getMessage());
        }
        return null;
    }
}
