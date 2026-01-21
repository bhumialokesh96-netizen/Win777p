package com.win777.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Win777Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Win777Application.class, args);
    }
}
