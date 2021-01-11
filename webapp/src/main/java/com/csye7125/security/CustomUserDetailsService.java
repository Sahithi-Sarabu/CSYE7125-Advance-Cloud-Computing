package com.csye7125.security;

import com.csye7125.model.User;
import com.csye7125.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userInfo = userRepository.findByUsername(username);
        if (userInfo == null) {
            throw new UsernameNotFoundException("User " + username + " was not found in database");
        }
        String password = userInfo.getPassword();
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
        grantedAuthorities.add(grantedAuthority);
        return new org.springframework.security.core.userdetails.User(username,
                password, grantedAuthorities);

    }


}