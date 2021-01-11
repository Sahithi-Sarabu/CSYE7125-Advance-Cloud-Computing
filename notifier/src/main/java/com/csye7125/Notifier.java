package com.csye7125;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class Notifier extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(Notifier.class,args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        builder.sources(this.getClass());
        return super.configure(builder);
    }
}
