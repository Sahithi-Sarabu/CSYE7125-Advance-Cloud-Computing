package com.csye7125.model;

import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface WatchRepository extends JpaRepository<Watch,String> {
    @Query("Select watch from Watch watch where watch.watch_id=?1 and watch.user_id=?2" )
    Watch findWatchByWatchIdAndUserId(String watch_id, String user_id);
    @Query("Select watch from Watch watch where watch.watch_id=?1" )
    Watch findWatchByWatchId(String watch_id);
    @Query("Select watch from Watch watch where watch.user_id=?1" )
    List<Watch> findAllByUserId(String user_id);


}
