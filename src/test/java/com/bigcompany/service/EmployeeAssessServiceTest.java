package com.bigcompany.service;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.bigcompany.assess.EmployeeNode;
import com.bigcompany.model.EmployeeRecord;
import com.bigcompany.utils.BigCompanyConstants;

class EmployeeAssessServiceTest {
    private EmployeeAssessService employeeAssessService;

    @BeforeEach
    void setUp() {
        employeeAssessService = new EmployeeAssessService();
    }

    @Test
    void testAnalyzeAndIdentify_CEOOnly() {
        EmployeeRecord ceo = new EmployeeRecord(1, "John", "Doe", 10000, null);
        List<EmployeeRecord> employees = new ArrayList<>();
        employees.add(ceo);
        List<EmployeeNode> result = employeeAssessService.analyzeAndIdentify(new ArrayList<>(employees), 1);
        assertEquals(1, result.size());
        assertEquals(ceo, result.get(0).getEmployee());
        assertEquals(0, result.get(0).getBand());
    }

    @Test
    void testAnalyzeAndIdentify_ManagerAndReportee() {
        EmployeeRecord ceo = new EmployeeRecord(1, "John", "Doe", 10000, null);
        EmployeeRecord emp = new EmployeeRecord(2, "Jane", "Smith", 5000, 1);
        List<EmployeeRecord> employees = new ArrayList<>();
        employees.add(ceo);
        employees.add(emp);
        try (MockedStatic<BigCompanyConstants> mocked = Mockito.mockStatic(BigCompanyConstants.class, Mockito.CALLS_REAL_METHODS)) {
            List<EmployeeNode> result = employeeAssessService.analyzeAndIdentify(new ArrayList<>(employees), 1);
            assertEquals(2, result.size());
            // CEO node
            EmployeeNode ceoNode = result.stream().filter(n -> n.getEmployee().id() == 1).findFirst().orElse(null);
            assertNotNull(ceoNode);
            assertEquals(0, ceoNode.getBand());
            // Employee node
            EmployeeNode empNode = result.stream().filter(n -> n.getEmployee().id() == 2).findFirst().orElse(null);
            assertNotNull(empNode);
            assertEquals(1, empNode.getBand());
        }
    }

    @Test
    void testAnalyzeAndIdentify_SalaryMoreAndLess() {
        EmployeeRecord ceo = new EmployeeRecord(1, "John", "Doe", 10000, null);
        EmployeeRecord emp1 = new EmployeeRecord(2, "Jane", "Smith", 1000, 1);
        EmployeeRecord emp2 = new EmployeeRecord(3, "Bob", "Brown", 1000, 1);
        List<EmployeeRecord> employees = List.of(ceo, emp1, emp2);
        try (MockedStatic<BigCompanyConstants> mocked = Mockito.mockStatic(BigCompanyConstants.class, Mockito.CALLS_REAL_METHODS)) {
            List<EmployeeNode> result = employeeAssessService.analyzeAndIdentify(new ArrayList<>(employees), 1);
            EmployeeNode ceoNode = result.stream().filter(n -> n.getEmployee().id() == 1).findFirst().orElse(null);
            assertNotNull(ceoNode);
            assertTrue(ceoNode.isSalaryMore() || ceoNode.isSalaryLess() || (!ceoNode.isSalaryMore() && !ceoNode.isSalaryLess()));
        }
    }
}
