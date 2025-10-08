package com.spendy.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;
    private String type;       // INCOME | EXPENSE
    private double amount;
    private String currency;   // "EUR"
    private String categoryId;
    private String method;     // CASH | CARD
    private LocalDate date;
    private String note;

    public Transaction() {}

    public Transaction(String id, String type, double amount, String currency,
                       String categoryId, String method, LocalDate date, String note) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.currency = currency;
        this.categoryId = categoryId;
        this.method = method;
        this.date = date;
        this.note = note;
    }

    public String getId() { return id; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public String getCategoryId() { return categoryId; }
    public String getMethod() { return method; }
    public LocalDate getDate() { return date; }
    public String getNote() { return note; }

    public void setId(String id) { this.id = id; }
    public void setType(String type) { this.type = type; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public void setMethod(String method) { this.method = method; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setNote(String note) { this.note = note; }
}