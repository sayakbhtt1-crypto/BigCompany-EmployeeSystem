package com.bigcompany.report;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bigcompany.assess.EmployeeNode;
import com.bigcompany.model.Employee;
import com.bigcompany.utils.BigCompanyConstants;

/**
 * Employee Reader for Big Company
 * @author Sayak Bhattacharya
 */
public class EmployeeReader {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeReader.class);

    public static void main(String[] args) {
        List<Employee> employees = readBigCompanyCsv(args);

        // init with CEO
        Employee ceoEmployee =
            employees.stream().filter( e -> Objects.isNull(e.getManagerId())).distinct().toList().getFirst();
        
        List<EmployeeNode> bigCompanyEmployeeNodes = analyzeAndIdentify(employees, ceoEmployee.getId());

        bigCompanyReport(bigCompanyEmployeeNodes);
        
    }//EOM

    /**
     * Generate report for big company employees
     * @param bigCompanyEmployeeNodes
     */
    private static void bigCompanyReport(List<EmployeeNode> bigCompanyEmployeeNodes){
        //
        int maxChain = 4;
        logger.info("Managers earning less than they should: ");
        bigCompanyEmployeeNodes.forEach( enode -> {
            if (enode.isSalaryLess())
                logger.info(BigCompanyConstants.EMPLOYEE_LOG_TEMPLATE, enode.getEmployee().getFirstName() + " " + enode.getEmployee().getLastName(), enode.getSalaryLess());  } );

        logger.info("Managers earning more than they should: ");
        bigCompanyEmployeeNodes.forEach( enode -> {
            if (enode.isSalaryMore())
                logger.info(BigCompanyConstants.EMPLOYEE_LOG_TEMPLATE, enode.getEmployee().getFirstName() + " " + enode.getEmployee().getLastName(), enode.getSalaryMore());  } );

        logger.info("Employees with too long reporting chains: ");
        bigCompanyEmployeeNodes.forEach( enode -> {
            if (enode.getBand() > 4)
                logger.info(BigCompanyConstants.EMPLOYEE_LOG_TEMPLATE, enode.getEmployee().getFirstName() + " " + enode.getEmployee().getLastName(), enode.getBand() - maxChain);  } );

    }//EOM

    /**
     * Read big company employee CSV file
     * @param args
     * @return List<Employee>
     */
    private static List<Employee> readBigCompanyCsv(String[] args){
        String path = BigCompanyConstants.EMPLOYEE_FILE_CSV;
        if (args.length > 0) path = args[0];

        List<Employee> employees = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String header = br.readLine();
            if (header == null) {
                logger.error("Empty file: {}", path);
                System.exit(2);}

            String line;
            int lineNo = 1;
            while ((line = br.readLine()) != null) {
                lineNo++;
                // split with limit to preserve empty trailing fields
                String[] parts = line.split(",", -1);
                if (parts.length < 5) {
                    logger.warn("Skipping malformed line {}: {}", lineNo, line);
                    continue;}
                    int id = Integer.parseInt(parts[0].trim());
                    String first = parts[1].trim();
                    String last = parts[2].trim();
                    int salary = Integer.parseInt(parts[3].trim());
                    Integer manager = null;
                    String mgr = parts[4].trim();
                    if (!mgr.isEmpty()) manager = Integer.parseInt(mgr);

                    employees.add(new Employee(id, first, last, salary, manager));
            }

        } catch (IOException e) {
            logger.error("Failed to read file '{}': {}", path, e.getMessage());
            return Collections.emptyList();
        }

        if (employees.isEmpty()) {
            logger.info("No employee records found in: {}", path);
            return Collections.emptyList();
        }else{
            logger.info("Successfully read {} employee records from: {}", employees.size(), path);
        }

        Collections.sort(employees, Comparator.comparingInt(Employee::getSalary));

        return employees;

    }

    /**
     * Analyze and identify big company employee aspects
     * @param employees list of all employees
     * @param ceoId CEO employee ID
     * @return List<EmployeeNode> post anlaysis result
     */
    private static List<EmployeeNode> analyzeAndIdentify(List<Employee> employees, int ceoId){ 
        int band = 0;
        List<Integer> employeeCheckList = new ArrayList<>();
        employeeCheckList.add(ceoId);
        List<Integer> nextBandCheckList = new ArrayList<>();
        List<Employee> directReportees = null;

        ArrayList<Integer> managerIds = getAllManagerIds(employees);

        List<EmployeeNode> bigCompanyEmployeeNodes = new ArrayList<>();
        
        while (!employees.isEmpty()){ 
            for (int mgrId: employeeCheckList){
                
                if (!managerIds.contains(mgrId)){
                    Employee worker = employees.stream().filter( e -> e.getId() == mgrId).findFirst().orElse(null);
                    bigCompanyEmployeeNodes.add(new EmployeeNode(worker, band));
                    employees.remove(worker);
                    managerIds.remove(Integer.valueOf(mgrId));
                    continue;
                } 
                
                directReportees = getDirectReportees(employees, mgrId);
                nextBandCheckList.addAll(directReportees.stream().map( e -> e.getId()).toList() );
                
                EmployeeNode mgrNode = new EmployeeNode();
                Employee mgrEmployee = 
                    employees.stream().filter( e -> e.getId() == mgrId).findFirst().orElse(null);
                
                mgrNode.setEmployee(mgrEmployee);
                
                //Average salary of direct reportees
                mgrNode.setAverageSubordinateSalary(directReportees.stream().mapToInt(Employee::getSalary).average().orElse(0.0));

                if (mgrNode.getEmployee().getSalary() > mgrNode.getAverageSubordinateSalary() * BigCompanyConstants.SALARY_FACTOR_MAX){
                    //salary is more than average of direct reportees                
                    mgrNode.setSalaryMore(true);
                    mgrNode.setSalaryMore((BigCompanyConstants.SALARY_FACTOR_MAX * mgrNode.getEmployee().getSalary()) - mgrNode.getAverageSubordinateSalary());
                    mgrNode.setSalaryLess(false);
                    mgrNode.setSalaryLess(0.0);
                }else if (mgrNode.getEmployee().getSalary() < mgrNode.getAverageSubordinateSalary() * BigCompanyConstants.SALARY_FACTOR_MIN){
                    //salary is less than average of direct reportees
                    mgrNode.setSalaryMore(false);
                    mgrNode.setSalaryMore(0.0);
                    mgrNode.setSalaryLess(true);
                    mgrNode.setSalaryLess((BigCompanyConstants.SALARY_FACTOR_MIN * mgrNode.getAverageSubordinateSalary()) - mgrNode.getEmployee().getSalary());
                }else{
                    //salary is within range                    
                    mgrNode.setSalaryMore(false);
                    mgrNode.setSalaryLess(false);
                    mgrNode.setSalaryMore(0.0);
                    mgrNode.setSalaryLess(0.0);
                    logger.info(BigCompanyConstants.EMPLOYEE_LOG_TEMPLATE, mgrNode.getEmployee().getFirstName() + " " + mgrNode.getEmployee().getLastName(), mgrNode.getSalaryLess());
                }
                
                mgrNode.setBand(band);
                bigCompanyEmployeeNodes.add(mgrNode);
                employees.remove(mgrEmployee);                                
            }
            if (nextBandCheckList.isEmpty()) break;//EOL reached
            employeeCheckList.clear();
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
    private static ArrayList<Integer> getAllManagerIds(List<Employee> employees) {
        return new ArrayList<>(employees.stream()
                .map(Employee::getManagerId)
                .filter(Objects::nonNull)
                .distinct()
                .toList());
    }
    
    /**
     * Get all direct reportees of a manager
     * @param employees list of all employees
     */
    private static List<Employee> getDirectReportees(List<Employee> employees, int managerId) {
        return employees.stream()
                .filter(e -> Objects.nonNull(e.getManagerId()) && e.getManagerId().equals(managerId))
                .toList();
    }//EOM

}//EOC
