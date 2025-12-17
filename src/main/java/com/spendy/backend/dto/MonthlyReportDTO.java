package com.spendy.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Reporte mensual con totales y agrupaciones")
public class MonthlyReportDTO {

    @Schema(description = "Año del reporte", example = "2025")
    public int year;

    @Schema(description = "Mes del reporte (1-12)", example = "11")
    public int month;

    @Schema(description = "Total de ingresos", example = "40.0")
    public double totalIncome;

    @Schema(description = "Total de gastos", example = "469.49")
    public double totalExpense;

    @Schema(description = "Balance (ingresos - gastos)", example = "-429.49")
    public double balance;

    @Schema(description = "Totales agrupados por método (CASH/CARD)")
    public Map<String, Double> byMethod;

    @Schema(description = "Totales agrupados por categoría")
    public List<CategoryTotalDTO> byCategory;

    public MonthlyReportDTO(int year, int month, double totalIncome, double totalExpense,
                            double balance, Map<String, Double> byMethod,
                            List<CategoryTotalDTO> byCategory) {
        this.year = year;
        this.month = month;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = balance;
        this.byMethod = byMethod;
        this.byCategory = byCategory;
    }
}