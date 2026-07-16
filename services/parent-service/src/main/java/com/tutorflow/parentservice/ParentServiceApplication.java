package com.tutorflow.parentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ParentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParentServiceApplication.class, args);
    }

}
