package com.bigcompany;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Main Application class for BigCompany Employee System
 * @author Sayak Bhattacharya
 */
@SpringBootApplication
public class EmployeeSystemApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeSystemApplication.class);
    public static void main(String[] args) {
        LOGGER.info("BigCompany Employee System Application started...");
        SpringApplication.run(EmployeeSystemApplication.class, args);
    }

    /**
     * Initialization marker after application startup
     */
    @PostConstruct
    public void onStartup() {
        LOGGER.info("BigCompany Employee System Application initialized successfully.");
    }

    /**
     * Cleanup marker before application shutdown
     */
    @PreDestroy
    public void onShutdown() {
        LOGGER.info("BigCompany Employee System Application is shutting down.");
    }

}
