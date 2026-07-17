package com.medical.assessment.notems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NoteMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoteMsApplication.class, args);
    }

}
