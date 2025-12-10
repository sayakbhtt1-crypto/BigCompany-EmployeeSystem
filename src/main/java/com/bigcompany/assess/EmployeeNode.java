package com.bigcompany.assess;

import com.bigcompany.model.Employee;

public class EmployeeNode {
    private Employee employee;
    private int band;
    private double averageSubordinateSalary;
    private boolean isSalaryMore;
    private double salaryMore;
    private boolean isSalaryLess;
    private double salaryLess;

    /**
     * For worker employees without subordinates
     */
    public EmployeeNode (Employee employee, int band){
        this.employee = employee;
        this.band = band;
        this.averageSubordinateSalary = 0.0;
        this.isSalaryMore = false;
        this.salaryMore = 0.0;
        this.isSalaryLess = false;
        this.salaryLess = 0.0;
    }

    public EmployeeNode (){}

    //Getters and Setters
    public Employee getEmployee() {
        return employee;}
    public void setEmployee(Employee employee) {
        this.employee = employee;}

    public int getBand() {
        return band;}
    public void setBand(int band) {
        this.band = band;}

    public double getAverageSubordinateSalary() {
        return averageSubordinateSalary;}
    public void setAverageSubordinateSalary(double averageSubordinateSalary) {
        this.averageSubordinateSalary = averageSubordinateSalary;}

    public boolean isSalaryMore() {
        return isSalaryMore;}
    public void setSalaryMore(boolean isSalaryMore) {
        this.isSalaryMore = isSalaryMore;}

    public double getSalaryMore() {
        return salaryMore;}
    public void setSalaryMore(double salaryMore) {
        this.salaryMore = Math.floor(salaryMore * 100) / 100;
    }

    public boolean isSalaryLess() {
        return isSalaryLess;}
    public void setSalaryLess(boolean isSalaryLess) {
        this.isSalaryLess = isSalaryLess;}

    public double getSalaryLess() {
        return salaryLess;}
    public void setSalaryLess(double salaryLess) {
        this.salaryLess = Math.floor(salaryLess * 100) / 100;}

    //ToString method
    @Override
    public String toString() {
        return String.format("Employee: %d, Band: %d, IsSalaryMore: %b, IsSalaryLess: %b, EmployeeName: %s",
        employee.getId(), band, isSalaryMore, isSalaryLess, employee.getFirstName() + " " + employee.getLastName());
    }
}

