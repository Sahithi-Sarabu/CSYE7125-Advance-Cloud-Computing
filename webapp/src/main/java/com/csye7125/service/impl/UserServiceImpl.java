package com.csye7125.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csye7125.model.User;
import com.csye7125.model.UserRepository;
import com.csye7125.service.UserService;
import io.prometheus.client.Histogram;
import io.prometheus.client.CollectorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

@Service("userService")
public class UserServiceImpl implements UserService {

    private final Histogram requestLatency;

    public UserServiceImpl(CollectorRegistry registry) {
        requestLatency = Histogram.build()
                .name("requests_latency_seconds_user").help("Request latency in seconds.").register(registry);
    }

    @Autowired
    UserRepository userRepository;

    ResponseEntity BadRequest = new ResponseEntity<>(JSONObject.parseObject(JSON.toJSONString(null)), HttpStatus.BAD_REQUEST);
    ResponseEntity NoContent = new ResponseEntity<>(JSONObject.parseObject(JSON.toJSONString(null)), HttpStatus.NO_CONTENT);


    @Override
    public ResponseEntity<JSONObject> getUserInfo() {
        User user;
        String currentUserID = getCurrentUserName();
        Histogram.Timer requestTimer = requestLatency.startTimer();
        try {
            user = userRepository.findByUsername(currentUserID);
        } finally {
            requestTimer.observeDuration();
        }
        return new ResponseEntity<>(JSONObject.parseObject(JSON.toJSONString(user)), HttpStatus.OK);

    }

    @Override
    public ResponseEntity<JSONObject> getUserInfo(String id) {
        if (userRepository.existsById(id)){
            Optional<User> u;
            Histogram.Timer requestTimer = requestLatency.startTimer();
            try {
                u  = userRepository.findById(id);
            } finally {
                requestTimer.observeDuration();
            }
            User user = u.get();
            return new ResponseEntity<>(JSONObject.parseObject(JSON.toJSONString(user)), HttpStatus.OK);
        }
        else return BadRequest;
    }
    @Override
    public ResponseEntity<JSONObject> updateUserInfo(User user) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserID = getCurrentUserName(principal);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal,
                SecurityContextHolder.getContext().getAuthentication().getCredentials());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User userOld;
        Histogram.Timer requestTimer = requestLatency.startTimer();
        try {
            userOld = userRepository.findByUsername(currentUserID);
        } finally {
            requestTimer.observeDuration();
        }
        if(user.getId()==null||user.getAccount_created()==null||user.getUsername()==null){
            userOld.setFirst_name(user.getFirst_name());
            userOld.setLast_name(user.getLast_name());
            userOld.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            userOld.setAccount_updated(String.valueOf(new Date()));
            requestTimer = requestLatency.startTimer();
            try {
                userRepository.save(userOld);
            } finally {
                requestTimer.observeDuration();
            }
            return NoContent;
        }
        else return BadRequest;


    }

    @Override
    public ResponseEntity<JSONObject> createUser(User user) {
        user.setAccount_created(String.valueOf(new Date()));
        user.setAccount_updated(String.valueOf(new Date()));
        //check email
        String emailAddress = user.getUsername();
        if (!checkEmailFormat(emailAddress)){
            return BadRequest;
        }
        // same email address
        if (userRepository.existsByUsername(emailAddress)){
            return BadRequest;}
        //weak password
        String password = user.getPassword();
        if (!checkPassword(password)){
            return BadRequest;
        }
        //encode
        String newPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPassword(newPassword);
        Histogram.Timer requestTimer = requestLatency.startTimer();
        try{
            userRepository.save(user);
        }catch (Exception e){
            return BadRequest;
        }finally {
            requestTimer.observeDuration();
        }
        return new ResponseEntity<>(JSONObject.parseObject(JSON.toJSONString(user)), HttpStatus.CREATED);
    }


    public static boolean checkPassword(String password){
        String pattern = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
        boolean isMatch = Pattern.matches(pattern,password);
        return isMatch;
    }

    public static boolean checkEmailFormat(String email){
        String pattern = "^(.+)@(.+)$";
        boolean isMatch = Pattern.matches(pattern,email);
        return isMatch;
    }

    public static String getCurrentUserName(){
        String currentUserID = "";
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal instanceof UserDetails) {
            currentUserID = ((UserDetails)principal).getUsername();
        }else {
            currentUserID = principal.toString();
        }
        return currentUserID;
    }

    public static String getCurrentUserName(Object principal){
        String currentUserID = "";
        if(principal instanceof UserDetails) {
            currentUserID = ((UserDetails)principal).getUsername();
        }else {
            currentUserID = principal.toString();
        }
        return currentUserID;
    }

    public String getCurrentUserID(){
        String currentUserName = UserServiceImpl.getCurrentUserName();
        User user = userRepository.findByUsername(currentUserName);
        return user.getId();
    }
}
