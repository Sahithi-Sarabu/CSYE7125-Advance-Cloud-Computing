package com.csye7125.model;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;

@Entity
@Table(name = "alert")
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class Alert {
    @Id
    @Column(length = 64)
    private String alert_id;
    @Column(name = "alert_created", updatable = false, nullable = false)
    private String alert_created;
    @Column(name = "alert_updated", nullable = false)
    private String alert_updated;
    @Enumerated(EnumType.STRING)
    private FieldType field_type;
    public enum FieldType {
        temp, feels_like, temp_min, temp_max, pressure, humidity
    }
    @Enumerated(EnumType.STRING)
    private Operator operator;
    public enum Operator {
        gt, gte, eq, lt, lte
    }

    private int value;

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

    public FieldType getField_type() {
        return field_type;
    }

    public void setField_type(FieldType field_type) {
        this.field_type = field_type;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}