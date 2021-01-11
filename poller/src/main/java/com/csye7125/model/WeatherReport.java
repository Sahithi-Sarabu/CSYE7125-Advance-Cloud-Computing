package com.csye7125.model;

import java.util.List;

public class WeatherReport {
    private String watch_id;
    private String user_id;
    private String watch_created;
    private String watch_updated;
    private String zipcode;
    private List<Alert> alerts;
    private String status;
    private float temp;
    private float temp_min;
    private float humidity;
    private float pressure;
    private float feels_like;
    private float temp_max;
    private List<String> deletedWatchIds;

    public String getWatch_id() {
        return watch_id;
    }

    public void setWatch_id(String watch_id) {
        this.watch_id = watch_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getWatch_created() {
        return watch_created;
    }

    public void setWatch_created(String watch_created) {
        this.watch_created = watch_created;
    }

    public String getWatch_updated() {
        return watch_updated;
    }

    public void setWatch_updated(String watch_updated) {
        this.watch_updated = watch_updated;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public float getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(float temp_min) {
        this.temp_min = temp_min;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(float feels_like) {
        this.feels_like = feels_like;
    }

    public float getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(float temp_max) {
        this.temp_max = temp_max;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getDeletedWatchIds() {
        return deletedWatchIds;
    }

    public void setDeletedWatchIds(List<String> deletedWatchIds) {
        this.deletedWatchIds = deletedWatchIds;
    }

    @Override
    public String toString() {
        return "WeatherReport{" +
                "watch_id='" + watch_id  +
                ", user_id='" + user_id +
                ", watch_created='" + watch_created +
                ", watch_updated='" + watch_updated  +
                ", zipcode='" + zipcode +
                ", alerts=" + alerts +
                ", status='" + status  +
                ", temp=" + temp +
                ", temp_min=" + temp_min +
                ", humidity=" + humidity +
                ", pressure=" + pressure +
                ", feels_like=" + feels_like +
                ", temp_max=" + temp_max +
                ", deletedWatchIds=" + deletedWatchIds +
                '}';
    }
}
