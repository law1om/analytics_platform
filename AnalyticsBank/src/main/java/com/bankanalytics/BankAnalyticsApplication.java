package com.bankanalytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
public class BankAnalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankAnalyticsApplication.class, args);
    }
}
