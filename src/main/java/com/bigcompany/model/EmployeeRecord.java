package com.bigcompany.model;

/**
 * Model class to represent big company employee record
 * @author Sayak Bhattacharya
 */
public record EmployeeRecord(
    int id,
    String firstName,
    String lastName,
    int salary,
    Integer managerId) {}