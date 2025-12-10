package com.bigcompany.service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.bigcompany.model.EmployeeRecord;
import com.bigcompany.utils.BigCompanyConstants;

class EmployeeReaderServiceTest {
    private EmployeeReaderService employeeReaderService;

    @BeforeEach
    void setUp() {
        employeeReaderService = new EmployeeReaderService();
    }

    @Test
    void testReadBigCompanyCsvSuccess(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("employees.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("id,first,last,salary,manager\n");
            writer.write("1,John,Doe,5000,\n");
            writer.write("2,Jane,Smith,4000,1\n");
            writer.close();
        }
        try (MockedStatic<BigCompanyConstants> mocked = Mockito.mockStatic(BigCompanyConstants.class, Mockito.CALLS_REAL_METHODS)) {
            //mocked.when(() -> BigCompanyConstants.EMPLOYEE_FILE_CSV).thenReturn(csvFile.toString());
            List<EmployeeRecord> employees = employeeReaderService.readBigCompanyCsv();
            assertEquals(2, employees.size());
            assertEquals("John", employees.get(1)); // Sorted by salary
            assertEquals("Jane", employees.get(0));
        }
    }

    @Test
    void testReadBigCompanyCsvEmptyFile(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("empty.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("");
        }
        try (MockedStatic<BigCompanyConstants> mocked = Mockito.mockStatic(BigCompanyConstants.class, Mockito.CALLS_REAL_METHODS)) {
            //mocked.when(() -> BigCompanyConstants.EMPLOYEE_FILE_CSV).thenReturn(csvFile.toString());
            List<EmployeeRecord> employees = employeeReaderService.readBigCompanyCsv();
            assertTrue(employees.isEmpty());
        }
    }

    @Test
    void testReadBigCompanyCsvMalformedLine(@TempDir Path tempDir) throws IOException {
        Path csvFile = tempDir.resolve("malformed.csv");
        try (FileWriter writer = new FileWriter(csvFile.toFile())) {
            writer.write("id,first,last,salary,manager\n");
            writer.write("1,John\n"); // Malformed
        }
        try (MockedStatic<BigCompanyConstants> mocked = Mockito.mockStatic(BigCompanyConstants.class, Mockito.CALLS_REAL_METHODS)) {
            //mocked.when(() -> BigCompanyConstants.EMPLOYEE_FILE_CSV).thenReturn(csvFile.toString());
            List<EmployeeRecord> employees = employeeReaderService.readBigCompanyCsv();
            assertTrue(employees.isEmpty());
        }
    }

    @Test
    void testReadBigCompanyCsvIOException(@TempDir Path tempDir) {
        Path csvFile = tempDir.resolve("missing.csv");
        try (MockedStatic<BigCompanyConstants> mocked = Mockito.mockStatic(BigCompanyConstants.class, Mockito.CALLS_REAL_METHODS)) {
            //mocked.when(() -> BigCompanyConstants.EMPLOYEE_FILE_CSV).thenReturn(csvFile.toString());
            List<EmployeeRecord> employees = employeeReaderService.readBigCompanyCsv();
            assertTrue(employees.isEmpty());
        }
    }
}
