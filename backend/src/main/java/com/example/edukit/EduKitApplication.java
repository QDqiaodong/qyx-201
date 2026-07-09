package com.example.edukit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EduKitApplication {

    public static void main(String[] args) {
        SpringApplication.run(EduKitApplication.class, args);
    }

}
