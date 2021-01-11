package com.csye7125.model;

import java.util.List;

public class Message {
    private String status;
    private String watch_id;
    private String user_id;
    private String watch_created;
    private String watch_updated;
    private String zipcode;
    private List<Alert> alerts;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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
}
