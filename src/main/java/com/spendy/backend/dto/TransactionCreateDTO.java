package com.spendy.backend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class TransactionCreateDTO {

    @NotBlank
    private String type; // INCOME | EXPENSE

    @Positive
    private double amount;

    @NotBlank
    private String currency; // EUR

    @NotBlank
    private String categoryId;

    @NotBlank
    private String method; // CASH | CARD

    @NotNull
    private LocalDate date;

    private String note;

    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCategoryId() { return categoryId; }
    public String getMethod() { return method; }
    public LocalDate getDate() { return date; }
    public String getNote() { return note; }

    public void setType(String type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public void setMethod(String method) { this.method = method; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setNote(String note) { this.note = note; }
}