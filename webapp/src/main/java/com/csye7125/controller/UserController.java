package com.csye7125.controller;

import com.alibaba.fastjson.JSONObject;
import com.csye7125.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.csye7125.service.UserService;
import io.prometheus.client.*;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    UserService userService;
    final Counter getUserInfoRequests;
    final Counter updateUserInfoRequests;
    final Counter createUserRequests;
    final Counter getUserInfoByIDRequests;


    public UserController(CollectorRegistry registry) {
        getUserInfoRequests = Counter.build().name("get_user_info").help("Get Self Info").register(registry);
        updateUserInfoRequests = Counter.build().name("update_user_info").help("Update Info").register(registry);
        createUserRequests = Counter.build().name("create_user").help("Create User").register(registry);
        getUserInfoByIDRequests = Counter.build().name("get_user_info_by_ID").help("Get User Info by ID").register(registry);
    }

    @GetMapping("v1/user/self")
    public ResponseEntity<JSONObject> getUserInfo() {
        getUserInfoRequests.inc();
        logger.info("Get Current User Information");
        return userService.getUserInfo();
    }

    @PutMapping("v1/user/self")
    public ResponseEntity<JSONObject> updateUserInfo(@RequestBody User user) {
        updateUserInfoRequests.inc();
        logger.info("Update User "+user.getUsername());
        return userService.updateUserInfo(user);
    }
        

    @PostMapping("v1/user")
    public ResponseEntity<JSONObject> createUser(@RequestBody User user){
        createUserRequests.inc();
        logger.info("Create User "+user.getUsername());
        return userService.createUser(user);
    }

    @GetMapping("v1/user/{id}")
    public ResponseEntity<JSONObject> getUserInfoByID(@PathVariable String id){
        getUserInfoByIDRequests.inc();
        logger.info("Get User "+id);
        return userService.getUserInfo(id);
    }

    @PostMapping("hello")
    public String hello(){
        return "hello";
    }

}