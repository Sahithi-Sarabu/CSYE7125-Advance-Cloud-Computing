package com.csye7125.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csye7125.model.User;
import com.csye7125.model.Watch;
import org.springframework.http.ResponseEntity;

public interface WatchService {

    ResponseEntity<JSONObject> createWatch(Watch watch);

    ResponseEntity<JSONObject> getWatchByID(String watch_id);

    ResponseEntity<JSONObject> updateWatch(String watch_id, Watch watch);

    ResponseEntity<JSONObject> deleteWatchByID(String watch_id);

    ResponseEntity<JSONArray> getWatches();
}
