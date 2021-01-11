package com.csye7125.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;

@Entity
@Table(name = "alert")
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
public class Alert {

    @Id
    private String alert_id;
    private String alert_created;
    private String alert_updated;
    private String field_type;
    private String operator;
    private String alert_status;
    private String alert_sent_time;
    private int number;
    @ManyToOne
    @JoinColumn(name="watch_id")
    private Watch watch;

    public Alert() {
    }

    public String getAlert_id() {
        return alert_id;
    }

    public void setAlert_id(String alert_id) {
        this.alert_id = alert_id;
    }

    public String getAlert_created() {
        return alert_created;
    }

    public void setAlert_created(String alert_created) {
        this.alert_created = alert_created;
    }

    public String getAlert_updated() {
        return alert_updated;
    }

    public void setAlert_updated(String alert_updated) {
        this.alert_updated = alert_updated;
    }

    public String getField_type() {
        return field_type;
    }

    public void setField_type(String filed_type) {
        this.field_type = filed_type;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getAlert_status() {
        return alert_status;
    }

    public void setAlert_status(String alert_status) {
        this.alert_status = alert_status;
    }

    public String getAlert_sent_time() {
        return alert_sent_time;
    }

    public void setAlert_sent_time(String alert_status_updated) {
        this.alert_sent_time = alert_status_updated;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Watch getWatch() {
        return watch;
    }

    public void setWatch(Watch watch) {
        this.watch = watch;
    }

    @Override
    public String toString() {
        return "Alerts{" +
                "alert_id='" + alert_id + '\'' +
                ", alert_created='" + alert_created + '\'' +
                ", alert_updated='" + alert_updated + '\'' +
                ", filed_type='" + field_type + '\'' +
                ", operator='" + operator + '\'' +
                ", alert_status='" + alert_status + '\'' +
                ", alert_status_updated='" + alert_sent_time + '\'' +
                ", number=" + number +
                '}';
    }
}
