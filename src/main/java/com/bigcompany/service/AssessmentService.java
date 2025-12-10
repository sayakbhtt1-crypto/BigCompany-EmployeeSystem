package com.bigcompany.service;

import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bigcompany.assess.EmployeeNode;
import com.bigcompany.model.EmployeeRecord;

/**
 * Service to orchestrate big company employee assessment
 * @author Sayak Bhattacharya
 */
@Service
public class AssessmentService {

    private final EmployeeAssessService employeeAssessService;
    private final EmployeeReportService employeeReportService;
    private final EmployeeReaderService employeeReaderService;

    public AssessmentService(EmployeeAssessService employeeAssessService,
                             EmployeeReportService employeeReportService,
                             EmployeeReaderService employeeReaderService) {
        this.employeeAssessService = employeeAssessService;
        this.employeeReportService = employeeReportService;
        this.employeeReaderService = employeeReaderService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(AssessmentService.class);

    public boolean orchestrate() {
        LOGGER.info("BigCompany Employee System orchestration started...");    
        try{
        List<EmployeeRecord> employees = employeeReaderService.readBigCompanyCsv();

        // init with CEO
        EmployeeRecord ceoEmployee =
            employees.stream().filter( e -> Objects.isNull(e.managerId())).distinct().toList().getFirst();
        
        List<EmployeeNode> bigCompanyEmployeeNodes = employeeAssessService.analyzeAndIdentify(employees, ceoEmployee.id());

        employeeReportService.bigCompanyReport(bigCompanyEmployeeNodes);
        return true;
        }catch(Exception e){
            LOGGER.error("BigCompany Employee System orchestration failed: {}", e.getMessage());    
            return false;
        }finally {
            LOGGER.info("BigCompany Employee System orchestration ended.");    
        }
        
    }//EOM
}
