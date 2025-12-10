package com.bigcompany.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.bigcompany.model.EmployeeRecord;
import com.bigcompany.utils.BigCompanyConstants;

/**
 * Service to read big company employee CSV file
 * @author Sayak Bhattacharya
 */
@Service
public class EmployeeReaderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeReaderService.class);

    /**
     * Read big company employee CSV file
     * @param args
     * @return List<Employee>
     */
    public List<EmployeeRecord> readBigCompanyCsv(){
        String path = BigCompanyConstants.EMPLOYEE_FILE_CSV;
        
        List<EmployeeRecord> employees = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String header = br.readLine();
            if (header == null) {
                LOGGER.error("Empty file: {}", path);
                System.exit(2);}

            String line;
            int lineNo = 1;
            while ((line = br.readLine()) != null) {
                lineNo++;
                // split with limit to preserve empty trailing fields
                String[] parts = line.split(",", -1);
                if (parts.length < 5) {
                    LOGGER.warn("Skipping malformed line {}: {}", lineNo, line);
                    continue;}
                    int id = Integer.parseInt(parts[0].trim());
                    String first = parts[1].trim();
                    String last = parts[2].trim();
                    int salary = Integer.parseInt(parts[3].trim());
                    Integer manager = null;
                    String mgr = parts[4].trim();
                    if (!mgr.isEmpty()) manager = Integer.valueOf(mgr);

                    employees.add(new EmployeeRecord(id, first, last, salary, manager));
            }

        } catch (IOException e) {
            LOGGER.error("Failed to read file '{}': {}", path, e.getMessage());
            return Collections.emptyList();
        }

        if (employees.isEmpty()) {
            LOGGER.info("No employee records found in: {}", path);
            return Collections.emptyList();
        }else{
            LOGGER.info("Successfully read {} employee records from: {}", employees.size(), path);
        }

        Collections.sort(employees, Comparator.comparingInt(e -> e.salary()));

        return employees;

    }

}
