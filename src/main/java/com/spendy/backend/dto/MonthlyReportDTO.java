package com.spendy.backend.dto;

import java.util.List;
import java.util.Map;

public class MonthlyReportDTO {
    public int year;
    public int month;
    public double totalIncome;
    public double totalExpense;
    public double balance;
    public Map<String, Double> byMethod; // CASH/CARD
    public List<CategoryTotalDTO> byCategory;

    public MonthlyReportDTO(int year, int month, double totalIncome, double totalExpense,
                            double balance, Map<String, Double> byMethod,
                            List<CategoryTotalDTO> byCategory) {
        this.year = year; this.month = month;
        this.totalIncome = totalIncome; this.totalExpense = totalExpense;
        this.balance = balance; this.byMethod = byMethod; this.byCategory = byCategory;
    }
}