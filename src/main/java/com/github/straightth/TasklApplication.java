package com.github.straightth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TasklApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasklApplication.class, args);
    }
}
