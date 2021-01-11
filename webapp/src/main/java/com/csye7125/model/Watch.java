package com.csye7125.model;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "watch")
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Watch {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String watch_id;
    private String user_id;
    @Column(name = "watch_created", updatable = false, nullable = false)
    private String watch_created;
    @Column(name = "watch_updated", nullable = false)
    private String watch_updated;
    private String zipcode;
    @OneToMany
    @JoinColumn(name = "watch_id")
    private List<Alert> alerts;


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

    public void setAlerts(List<Alert> alert) {
        this.alerts = alert;
    }
}
