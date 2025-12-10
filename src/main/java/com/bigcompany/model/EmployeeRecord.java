package com.bigcompany.model;
public record EmployeeRecord(
    int id,
    String firstName,
    String lastName,
    int salary,
    Integer managerId) {}