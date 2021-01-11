package com.csye7125.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert,String> {

    @Query("select a from Alert a, Watch w where a.watch = w.watch_id and w.watch_id in ?1 order by a.alert_updated")
    List<Alert> findAllByWatch(List<String> watchIdList);

    @Query("select a from Alert a, Watch w where a.watch = w.watch_id and w.watch_id = ?1")
    List<Alert> findAllByWatch_id(String watchId);
}
