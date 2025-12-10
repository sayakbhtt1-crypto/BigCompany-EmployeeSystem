package com.bigcompany.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bigcompany.assess.EmployeeNode;
import com.bigcompany.utils.BigCompanyConstants;
/**
 * Service to print report for big company employees
 * @author Sayak Bhattacharya
 */
@Service
public class EmployeeReportService {

    @SuppressWarnings("unused")
    private EmployeeReportService() {
        // private constructor to prevent instantiation
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeReportService.class);
    /**
     * Generate report for big company employees
     * @param bigCompanyEmployeeNodes
     */
    public void bigCompanyReport(List<EmployeeNode> bigCompanyEmployeeNodes){
        //
        int maxChain = 4;
        LOGGER.info("Managers earning less than they should: ");
        bigCompanyEmployeeNodes.forEach( enode -> {
            if (enode.isSalaryLess())
                LOGGER.info(BigCompanyConstants.EMPLOYEE_LOG_TEMPLATE, enode.getEmployee().firstName() + " " + enode.getEmployee().lastName(), enode.getSalaryLess());  } );

        LOGGER.info("Managers earning more than they should: ");
        bigCompanyEmployeeNodes.forEach( enode -> {
            if (enode.isSalaryMore())
                LOGGER.info(BigCompanyConstants.EMPLOYEE_LOG_TEMPLATE, enode.getEmployee().firstName() + " " + enode.getEmployee().lastName(), enode.getSalaryMore());  } );

        LOGGER.info("Employees with too long reporting chains: ");
        bigCompanyEmployeeNodes.forEach( enode -> {
            if (enode.getBand() > 4)
                LOGGER.info(BigCompanyConstants.EMPLOYEE_LOG_TEMPLATE, enode.getEmployee().firstName() + " " + enode.getEmployee().lastName(), enode.getBand() - maxChain);  } );

    }//EOM
}
