package com.bigcompany.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bigcompany.assess.EmployeeNode;
import com.bigcompany.model.EmployeeRecord;
import com.bigcompany.utils.BigCompanyConstants;

@Service
public class EmployeeAssessService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeAssessService.class);

    /**
     * Analyze and identify big company employee aspects
     * @param employees list of all employees
     * @param ceoId CEO employee ID
     * @return List<EmployeeNode> post anlaysis result
     */
    public List<EmployeeNode> analyzeAndIdentify(List<EmployeeRecord> employees, int ceoId){ 
        LOGGER.info("Employees being analyzed and identified...");
        int band = 0;
        List<Integer> employeeCheckList = new ArrayList<>();
        employeeCheckList.add(ceoId);
        List<Integer> nextBandCheckList = new ArrayList<>();
        List<EmployeeRecord> directReportees;

        ArrayList<Integer> managerIds = getAllManagerIds(employees);

        List<EmployeeNode> bigCompanyEmployeeNodes = new ArrayList<>();
        
        while (!employees.isEmpty()){ 
            for (int mgrId: employeeCheckList){
                
                if (!managerIds.contains(mgrId)){
                    //Not a manager but a worker
                    EmployeeRecord worker = employees.stream().filter( e -> e.id() == mgrId).findFirst().orElse(null);
                    bigCompanyEmployeeNodes.add(new EmployeeNode(worker, band));
                    employees.remove(worker);
                    managerIds.remove(Integer.valueOf(mgrId));
                    continue;//jump to next employee
                } 
                
                directReportees = getDirectReportees(employees, mgrId);
                nextBandCheckList.addAll(directReportees.stream().map( e -> e.id()).toList() );
                
                EmployeeNode mgrNode = new EmployeeNode();
                EmployeeRecord mgrEmployee = 
                    employees.stream().filter( e -> e.id() == mgrId).findFirst().orElse(null);
                
                mgrNode.setEmployee(mgrEmployee);
                
                //Average salary of direct reportees
                mgrNode.setAverageSubordinateSalary(directReportees.stream().mapToInt(EmployeeRecord::salary).average().orElse(0.0));

                if (mgrNode.getEmployee().salary() > mgrNode.getAverageSubordinateSalary() * BigCompanyConstants.SALARY_FACTOR_MAX){
                    //salary is more than average of direct reportees                
                    mgrNode.setSalaryMore(true);
                    mgrNode.setSalaryMore((BigCompanyConstants.SALARY_FACTOR_MAX * mgrNode.getEmployee().salary()) - mgrNode.getAverageSubordinateSalary());
                    mgrNode.setSalaryLess(false);
                    mgrNode.setSalaryLess(0.0);
                }else if (mgrNode.getEmployee().salary() < mgrNode.getAverageSubordinateSalary() * BigCompanyConstants.SALARY_FACTOR_MIN){
                    //salary is less than average of direct reportees
                    mgrNode.setSalaryMore(false);
                    mgrNode.setSalaryMore(0.0);
                    mgrNode.setSalaryLess(true);
                    mgrNode.setSalaryLess((BigCompanyConstants.SALARY_FACTOR_MIN * mgrNode.getAverageSubordinateSalary()) - mgrNode.getEmployee().salary());
                }else{
                    //salary is within range                    
                    mgrNode.setSalaryMore(false);
                    mgrNode.setSalaryLess(false);
                    mgrNode.setSalaryMore(0.0);
                    mgrNode.setSalaryLess(0.0);
                    LOGGER.info(BigCompanyConstants.EMPLOYEE_LOG_TEMPLATE, mgrNode.getEmployee().firstName() + " " + mgrNode.getEmployee().lastName(), mgrNode.getSalaryLess());
                }
                
                mgrNode.setBand(band);
                bigCompanyEmployeeNodes.add(mgrNode);
                employees.remove(mgrEmployee);                                
            }
            if (nextBandCheckList.isEmpty()) break;//EOL reached
            employeeCheckList.clear();
            LOGGER.info("Employees assessed in band: {}", band);
            band = band+1;
            employeeCheckList.addAll(nextBandCheckList);
            nextBandCheckList.clear();
        }

        return bigCompanyEmployeeNodes;
    }//EOM

    /**
     * Get all distinct manager IDs from employee list
     * @param employees list of all employees
     */
    private ArrayList<Integer> getAllManagerIds(List<EmployeeRecord> employees) {
        return new ArrayList<>(employees.stream()
                .map(EmployeeRecord::managerId)
                .filter(Objects::nonNull)
                .distinct()
                .toList());
    }//EOM
    
    /**
     * Get all direct reportees of a manager
     * @param employees list of all employees
     */
    private List<EmployeeRecord> getDirectReportees(List<EmployeeRecord> employees, int managerId) {
        return employees.stream()
                .filter(e -> Objects.nonNull(e.managerId()) && e.managerId().equals(managerId))
                .toList();
    }//EOM
}
