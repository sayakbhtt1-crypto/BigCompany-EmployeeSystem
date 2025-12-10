package com.bigcompany.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigcompany.assess.EmployeeNode;
import com.bigcompany.model.EmployeeRecord;
import com.bigcompany.utils.BigCompanyConstants;

class EmployeeReportServiceTest {
    private EmployeeReportService employeeReportService;

    @BeforeEach
    void setUp() {
        // Use reflection to instantiate due to private constructor
        try {
            var constructor = EmployeeReportService.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            employeeReportService = constructor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testBigCompanyReport_LogsCorrectly() {
        EmployeeRecord emp1 = new EmployeeRecord(1, "John", "Doe", 10000, null);
        EmployeeNode node1 = new EmployeeNode();
        node1.setEmployee(emp1);
        node1.setSalaryLess(true);
        node1.setSalaryLess(500.0);
        node1.setBand(2);

        EmployeeRecord emp2 = new EmployeeRecord(2, "Jane", "Smith", 12000, 1);
        EmployeeNode node2 = new EmployeeNode();
        node2.setEmployee(emp2);
        node2.setSalaryMore(true);
        node2.setSalaryMore(1000.0);
        node2.setBand(3);

        EmployeeRecord emp3 = new EmployeeRecord(3, "Bob", "Brown", 8000, 2);
        EmployeeNode node3 = new EmployeeNode();
        node3.setEmployee(emp3);
        node3.setBand(5); // Too long reporting chain

        List<EmployeeNode> nodes = List.of(node1, node2, node3);

        try (MockedStatic<LoggerFactory> loggerFactoryMocked = Mockito.mockStatic(LoggerFactory.class);
             MockedStatic<BigCompanyConstants> constantsMocked = Mockito.mockStatic(BigCompanyConstants.class, Mockito.CALLS_REAL_METHODS)) {
            Logger logger = Mockito.mock(Logger.class);
            loggerFactoryMocked.when(() -> LoggerFactory.getLogger(EmployeeReportService.class)).thenReturn(logger);
            //constantsMocked.when(() -> BigCompanyConstants.EMPLOYEE_LOG_TEMPLATE).thenReturn("{} - {}\n");

            employeeReportService.bigCompanyReport(nodes);

            Mockito.verify(logger, Mockito.atLeastOnce()).info(Mockito.anyString(), Mockito.any(), Mockito.any());
        }
    }
}
