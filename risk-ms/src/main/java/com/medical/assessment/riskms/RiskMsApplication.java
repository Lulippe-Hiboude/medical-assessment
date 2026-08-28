package com.medical.assessment.riskms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RiskMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskMsApplication.class, args);
    }

}
