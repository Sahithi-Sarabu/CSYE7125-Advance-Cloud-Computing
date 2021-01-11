package com.csye7125.service;

import com.alibaba.fastjson.JSONObject;
import com.csye7125.model.User;
import org.springframework.http.ResponseEntity;

public interface UserService {

    ResponseEntity<JSONObject> getUserInfo();

    ResponseEntity<JSONObject> getUserInfo(String id);

    ResponseEntity<JSONObject> updateUserInfo(User user);

    ResponseEntity<JSONObject> createUser(User user);

}
