package com.csye7125.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.persistence.*;

@Entity
@Table(name = "user")
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
@GenericGenerator(name = "jpa-uuid", strategy = "uuid")
public class User {
    @Id
    @GeneratedValue(generator = "jpa-uuid")
    @Column(length = 32)
    private String id;
    private String first_name;
    private String last_name;
    @JSONField(serialize = false)
    private String password;
    private String username;
    @Column(name = "account_created", updatable = false, nullable = false)
    private String account_created;
    @Column(name = "account_updated", nullable = false)
    private String account_updated;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getAccount_created() {
        return account_created;
    }

    public void setAccount_created(String account_created) {
        this.account_created = account_created;
    }

    public String getAccount_updated() {
        return account_updated;
    }

    public void setAccount_updated(String account_updated) {
        this.account_updated = account_updated;
    }

//    @Override
//    public String toString(){
//        return this.username;
//    }

}
