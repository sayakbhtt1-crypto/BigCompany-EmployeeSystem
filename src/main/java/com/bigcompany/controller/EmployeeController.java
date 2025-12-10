package com.bigcompany.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bigcompany.service.AssessmentService;


@RestController
@RequestMapping("/bigcompany/assessment")
public class EmployeeController {

    private final AssessmentService assessmentService;

    public EmployeeController(AssessmentService assessmentService) {
        // Default constructor
        this.assessmentService = assessmentService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeController.class);

    @GetMapping("/test")
    public String healthCheck() {
        LOGGER.info("BigCompany Employee System Application is up!!!");
        return "Employee System Application is up.";
    }

    @GetMapping("/assess")
    public void assessEmployees() {
        LOGGER.info("BigCompany Employee System assessment started...");     
        assessmentService.orchestrate();
    }

    
}