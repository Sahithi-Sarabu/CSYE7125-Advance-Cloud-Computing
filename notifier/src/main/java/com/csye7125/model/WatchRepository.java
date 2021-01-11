package com.csye7125.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface WatchRepository extends JpaRepository<Watch,String> {
    List<Watch> findAllByZipcode(String zipcode);

    @Query("select w from Watch w where w.watch_id in ?1")
    List<Watch> findAllByWatch_id(List<String> watchIds);

    @Query("select w from Watch w where w.watch_id = ?1")
    Watch findByWatchId(String watchId);
}