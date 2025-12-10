package com.bigcompany.service;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.bigcompany.assess.EmployeeNode;
import com.bigcompany.model.EmployeeRecord;

class AssessmentServiceTest {

    @Mock
    private EmployeeAssessService employeeAssessService;
    @Mock
    private EmployeeReportService employeeReportService;
    @Mock
    private EmployeeReaderService employeeReaderService;

    @InjectMocks
    private AssessmentService assessmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testOrchestrateSuccess() {
        EmployeeRecord ceo = mock(EmployeeRecord.class);
        when(ceo.managerId()).thenReturn(null);
        when(ceo.id()).thenReturn(1);
        List<EmployeeRecord> employees = List.of(ceo);
        List<EmployeeNode> nodes = Collections.emptyList();

        when(employeeReaderService.readBigCompanyCsv()).thenReturn(employees);
        when(employeeAssessService.analyzeAndIdentify(employees, 1)).thenReturn(nodes);

        boolean result = assessmentService.orchestrate();
        assertTrue(result);
        verify(employeeReportService).bigCompanyReport(nodes);
    }

    @Test
    void testOrchestrateFailure() {
        when(employeeReaderService.readBigCompanyCsv()).thenThrow(new RuntimeException("fail"));
        boolean result = assessmentService.orchestrate();
        assertFalse(result);
    }
}
