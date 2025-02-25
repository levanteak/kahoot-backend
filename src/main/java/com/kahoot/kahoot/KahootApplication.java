package com.kahoot.kahoot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KahootApplication {


    public static void main(String[] args) {
        SpringApplication.run(KahootApplication.class, args);
    }

}
